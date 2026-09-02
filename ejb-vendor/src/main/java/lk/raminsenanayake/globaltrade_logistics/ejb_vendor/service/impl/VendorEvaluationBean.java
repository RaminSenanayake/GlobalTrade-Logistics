package lk.raminsenanayake.globaltrade_logistics.ejb_vendor.service.impl;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.VendorScorecard;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.VendorComplianceException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.vendor.VendorEvaluationServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.VendorValidationInterceptor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.*;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.VendorPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
@Interceptors({AuditLoggingInterceptor.class, PerformanceMonitoringInterceptor.class})
public class VendorEvaluationBean implements VendorEvaluationServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(VendorEvaluationBean.class.getName());

    @Inject
    private VendorPersistenceService vendorService;

    @Inject
    private ShipmentPersistenceService shipmentService;

    @Inject
    private AlertPersistenceService alertService;

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public Vendor registerVendor(Vendor vendor) {
        if (vendor == null) {
            throw new VendorComplianceException("Vendor data cannot be null");
        }

        if (vendor.getVendorCode() == null || vendor.getVendorCode().trim().isEmpty()) {
            vendor.setVendorCode("VND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        if (vendor.getComplianceStatus() == null) {
            vendor.setComplianceStatus(VendorComplianceStatus.COMPLIANT);
        }

        vendor.setPerformanceRating(5.0);
        vendor.setOnTimeDeliveryRate(100.0);
        vendor.setLastEvaluatedAt(LocalDateTime.now());

        Vendor saved = vendorService.save(vendor);
        LOGGER.info("Registered vendor: " + saved.getName() + " with code: " + saved.getVendorCode());
        return saved;
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public VendorScorecard evaluateVendor(String vendorCode) {
        Optional<Vendor> optVendor = vendorService.findByVendorCode(vendorCode);
        if (optVendor.isEmpty()) {
            throw new VendorComplianceException("Vendor not found for evaluation: " + vendorCode);
        }

        Vendor vendor = optVendor.get();
        List<Shipment> allShipments = shipmentService.findAll();
        List<Shipment> vendorShipments = allShipments.stream()
                .filter(s -> vendorCode.equalsIgnoreCase(s.getAssignedVendor()))
                .toList();

        int total = vendorShipments.size();
        int delayed = (int) vendorShipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.DELAYED)
                .count();

        double onTimeRate = total == 0 ? 100.0 : ((double) (total - delayed) / total) * 100.0;
        double rating = total == 0 ? 5.0 : Math.max(1.0, Math.min(5.0, 5.0 - (delayed * 0.5)));

        VendorComplianceStatus compliance = VendorComplianceStatus.COMPLIANT;
        String recommendation = "Vendor in good standing. Suitable for critical dispatches.";

        if (onTimeRate < 70.0 || rating < 3.0) {
            compliance = VendorComplianceStatus.SUSPENDED;
            recommendation = "Performance severely degraded. Suspend assignments pending review.";
            if (alertService != null) {
                alertService.recordAlert(
                        SupplyChainAlertType.VENDOR_PERFORMANCE_DEGRADED,
                        SupplyChainAlertSeverity.HIGH,
                        "Vendor Suspended: " + vendor.getName(),
                        "Vendor " + vendorCode + " has fallen below SLA thresholds (On-Time: " + String.format("%.1f", onTimeRate) + "%)",
                        vendorCode
                );
            }
        } else if (onTimeRate < 85.0 || rating < 4.0) {
            compliance = VendorComplianceStatus.PROBATION;
            recommendation = "Performance on probation. Monitor delivery timelines.";
        }

        vendor.setOnTimeDeliveryRate(onTimeRate);
        vendor.setPerformanceRating(rating);
        vendor.setComplianceStatus(compliance);
        vendor.setTotalShipmentsHandled(total);
        vendor.setDelayedShipmentsCount(delayed);
        vendor.setLastEvaluatedAt(LocalDateTime.now());

        vendorService.save(vendor);

        LOGGER.info("Evaluated Vendor " + vendorCode + ": On-Time=" + onTimeRate + "%, Rating=" + rating + ", Handled=" + total);

        return new VendorScorecard(
                vendorCode,
                vendor.getName(),
                rating,
                onTimeRate,
                total,
                delayed,
                compliance,
                recommendation
        );
    }

    @Override
    @PermitAll
    public VendorScorecard getVendorScorecard(String vendorCode) {
        Optional<Vendor> optVendor = vendorService.findByVendorCode(vendorCode);
        if (optVendor.isEmpty()) {
            throw new VendorComplianceException("Vendor not found: " + vendorCode);
        }
        Vendor v = optVendor.get();
        String recommendation = v.getComplianceStatus() == VendorComplianceStatus.COMPLIANT
                ? "Active & eligible for all routes."
                : "Restricted routing applied.";

        return new VendorScorecard(
                v.getVendorCode(),
                v.getName(),
                v.getPerformanceRating(),
                v.getOnTimeDeliveryRate(),
                v.getTotalShipmentsHandled(),
                v.getDelayedShipmentsCount(),
                v.getComplianceStatus(),
                recommendation
        );
    }

    @Override
    @PermitAll
    public List<Vendor> getAllVendors() {
        return vendorService.findAll();
    }

    @Override
    @PermitAll
    public List<Vendor> getVendorsByStatus(VendorComplianceStatus status) {
        return vendorService.findByComplianceStatus(status);
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    @Interceptors({VendorValidationInterceptor.class})
    public void assignVendorToShipment(String trackingNumber, String vendorCode) {
        Optional<Vendor> optVendor = vendorService.findByVendorCode(vendorCode);
        if (optVendor.isEmpty()) {
            throw new VendorComplianceException("Vendor code does not exist: " + vendorCode);
        }

        Vendor vendor = optVendor.get();
        if (vendor.getComplianceStatus() == VendorComplianceStatus.SUSPENDED) {
            throw new VendorComplianceException("Cannot assign suspended vendor " + vendorCode + " to shipment " + trackingNumber);
        }

        Optional<Shipment> optShipment = shipmentService.findByTrackingNumber(trackingNumber);
        if (optShipment.isEmpty()) {
            throw new VendorComplianceException("Shipment does not exist: " + trackingNumber);
        }

        Shipment shipment = optShipment.get();
        shipment.setAssignedVendor(vendorCode);
        shipmentService.save(shipment);

        LOGGER.info("Assigned vendor " + vendorCode + " to shipment " + trackingNumber);
    }
}
