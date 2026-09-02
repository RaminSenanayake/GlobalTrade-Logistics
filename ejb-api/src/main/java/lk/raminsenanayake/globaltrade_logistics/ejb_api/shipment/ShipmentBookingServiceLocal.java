package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.BookingItemDto;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.BookingSummary;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;

@Local
public interface ShipmentBookingServiceLocal {

    void startBooking(String senderUsername, String origin, String destination);

    void addItem(String sku, String description, int quantity, double weightKg, double declaredValue);

    void removeItem(String sku);

    void selectCarrier(String carrierCode, String serviceLevel);

    BookingSummary getCurrentSummary();

    Shipment confirmBooking();

    void cancelBooking();
}
