package lk.raminsenanayake.globaltrade_logistics.ejb_customs.service.impl;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.customs.CustomsComplianceServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.CustomsClearanceException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.ShipmentNotFoundException;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.RegulatoryComplianceInterceptor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.*;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.CustomsPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
@Interceptors({AuditLoggingInterceptor.class, RegulatoryComplianceInterceptor.class, PerformanceMonitoringInterceptor.class})
public class CustomsComplianceBean implements CustomsComplianceServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(CustomsComplianceBean.class.getName());

    @Inject
    private CustomsPersistenceService customsService;

    @Inject
    private ShipmentPersistenceService shipmentService;

    @Inject
    private AlertPersistenceService alertService;

    @Override
    @RolesAllowed({"ADMIN", "CUSTOM_OFFICIAL", "LOGISTIC_PERSONNEL", "VENDOR"})
    public CustomsDeclaration submitDeclaration(CustomsDeclaration declaration) {
        if (declaration == null) {
            throw new CustomsClearanceException("Customs declaration data cannot be null");
        }

        if (declaration.getTrackingNumber() == null || declaration.getTrackingNumber().trim().isEmpty()) {
            throw new CustomsClearanceException("Tracking number is required for customs declaration");
        }

        Optional<Shipment> optShipment = shipmentService.findByTrackingNumber(declaration.getTrackingNumber());
        if (optShipment.isEmpty()) {
            throw new ShipmentNotFoundException("Cannot file customs declaration: shipment " + declaration.getTrackingNumber() + " does not exist");
        }

        if (declaration.getDeclarationNumber() == null || declaration.getDeclarationNumber().trim().isEmpty()) {
            declaration.setDeclarationNumber("DEC-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        }

        if (declaration.getFilingDeadline() == null) {
            declaration.setFilingDeadline(LocalDateTime.now().plusHours(72));
        }

        if (declaration.getStatus() == null) {
            declaration.setStatus(CustomsDeclarationStatus.SUBMITTED);
        }

        CustomsDeclaration saved = customsService.save(declaration);

        // Update shipment status to reflect pending customs clearance
        shipmentService.updateStatus(declaration.getTrackingNumber(), ShipmentStatus.PENDING_CLEARANCE);

        LOGGER.info("Filed Customs Declaration: " + saved.getDeclarationNumber() + " for Shipment: " + saved.getTrackingNumber());
        return saved;
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOM_OFFICIAL"})
    public void reviewDeclaration(String declarationNumber, CustomsDeclarationStatus status, String reviewedBy, String notes) {
        Optional<CustomsDeclaration> optDecl = customsService.findByDeclarationNumber(declarationNumber);
        if (optDecl.isEmpty()) {
            throw new CustomsClearanceException("Customs declaration not found: " + declarationNumber);
        }

        CustomsDeclaration decl = optDecl.get();
        customsService.updateStatus(declarationNumber, status, reviewedBy, notes);

        if (status == CustomsDeclarationStatus.APPROVED) {
            shipmentService.updateStatus(decl.getTrackingNumber(), ShipmentStatus.IN_TRANSIT);
            LOGGER.info("Customs declaration " + declarationNumber + " APPROVED by " + reviewedBy);
        } else if (status == CustomsDeclarationStatus.REJECTED) {
            shipmentService.updateStatus(decl.getTrackingNumber(), ShipmentStatus.CUSTOMS_HOLD);
            if (alertService != null) {
                alertService.recordAlert(
                        SupplyChainAlertType.CUSTOMS_HOLD,
                        SupplyChainAlertSeverity.HIGH,
                        "Customs Declaration Rejected",
                        "Declaration " + declarationNumber + " rejected. Notes: " + notes,
                        decl.getTrackingNumber()
                );
            }
            LOGGER.warning("Customs declaration " + declarationNumber + " REJECTED by " + reviewedBy);
        }
    }

    @Override
    @PermitAll
    public boolean checkCompliance(String trackingNumber) {
        Optional<CustomsDeclaration> optDecl = customsService.findByTrackingNumber(trackingNumber);
        return optDecl.isPresent() && optDecl.get().getStatus() == CustomsDeclarationStatus.APPROVED;
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOM_OFFICIAL", "LOGISTIC_PERSONNEL"})
    public List<CustomsDeclaration> getPendingDeclarations() {
        return customsService.findByStatus(CustomsDeclarationStatus.SUBMITTED);
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOM_OFFICIAL", "LOGISTIC_PERSONNEL"})
    public List<CustomsDeclaration> getApproachingDeadlineDeclarations(int hoursAhead) {
        LocalDateTime threshold = LocalDateTime.now().plusHours(hoursAhead > 0 ? hoursAhead : 24);
        return customsService.findApproachingDeadlines(threshold);
    }
}
