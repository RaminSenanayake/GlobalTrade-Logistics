package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.ShipmentNotFoundException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.ValidationException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.ShipmentTrackingServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.RegulatoryComplianceInterceptor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentItem;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
@Interceptors({AuditLoggingInterceptor.class, RegulatoryComplianceInterceptor.class, PerformanceMonitoringInterceptor.class})
public class ShipmentTrackingBean implements ShipmentTrackingServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(ShipmentTrackingBean.class.getName());

    @Inject
    private ShipmentPersistenceService shipmentService;

    @Inject
    private AlertPersistenceService alertService;

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL", "CUSTOMER"})
    public Shipment createShipment(Shipment shipment, List<ShipmentItem> items) {
        if (shipment == null) {
            throw new ValidationException("Shipment data cannot be null");
        }

        if (shipment.getTrackingNumber() == null || shipment.getTrackingNumber().trim().isEmpty()) {
            shipment.setTrackingNumber("GTL-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        }

        if (shipment.getStatus() == null) {
            shipment.setStatus(ShipmentStatus.CREATED);
        }

        if (shipment.getEstimatedDelivery() == null) {
            shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(5));
        }

        if (items != null) {
            for (ShipmentItem item : items) {
                item.setShipment(shipment);
                shipment.getItems().add(item);
            }
        }

        Shipment saved = shipmentService.save(shipment);
        LOGGER.info("Created shipment with tracking number: " + saved.getTrackingNumber());
        return saved;
    }

    @Override
    @PermitAll
    public Shipment getShipmentByTrackingNumber(String trackingNumber) {
        Optional<Shipment> opt = shipmentService.findByTrackingNumber(trackingNumber);
        if (opt.isEmpty()) {
            throw new ShipmentNotFoundException("Shipment with tracking number " + trackingNumber + " was not found");
        }
        return opt.get();
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL", "CUSTOMER"})
    public List<Shipment> getShipmentsBySender(String senderUsername) {
        return shipmentService.findBySender(senderUsername);
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public List<Shipment> getAllShipments() {
        return shipmentService.findAll();
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL", "VENDOR"})
    public void updateShipmentStatus(String trackingNumber, ShipmentStatus newStatus, String updatedBy) {
        Optional<Shipment> opt = shipmentService.findByTrackingNumber(trackingNumber);
        if (opt.isEmpty()) {
            throw new ShipmentNotFoundException("Cannot update status: Shipment " + trackingNumber + " does not exist");
        }

        shipmentService.updateStatus(trackingNumber, newStatus);
        LOGGER.info("Updated status of shipment " + trackingNumber + " to " + newStatus + " by " + updatedBy);
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public List<Shipment> detectPotentialDelays() {
        return shipmentService.findPotentialDelays(LocalDateTime.now());
    }
}
