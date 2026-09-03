package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import jakarta.ejb.Local;
import jakarta.json.JsonPatch;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Local
public interface ShipmentPersistenceService {
    Shipment save(Shipment shipment);
    Optional<Shipment> findById(Long id);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findAll();
    List<Shipment> findBySender(String senderUsername);
    List<Shipment> findByStatus(ShipmentStatus status);
    List<Shipment> findPotentialDelays(LocalDateTime thresholdTime);
    void update(Long id, JsonPatch jsonPatch);
    void updateStatus(String trackingNumber, ShipmentStatus newStatus);
    void delete(Long id);
}
