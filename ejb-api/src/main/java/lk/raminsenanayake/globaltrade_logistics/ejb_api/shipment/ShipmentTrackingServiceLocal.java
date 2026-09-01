package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentItem;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;

import java.util.List;

@Local
public interface ShipmentTrackingServiceLocal {

    Shipment createShipment(Shipment shipment, List<ShipmentItem> items);

    Shipment getShipmentByTrackingNumber(String trackingNumber);

    List<Shipment> getShipmentsBySender(String senderUsername);

    List<Shipment> getAllShipments();

    void updateShipmentStatus(String trackingNumber, ShipmentStatus newStatus, String updatedBy);

    List<Shipment> detectPotentialDelays();
}
