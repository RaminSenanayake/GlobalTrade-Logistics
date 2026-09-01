package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;

import java.io.Serializable;
import java.util.List;

@Local
public interface ShipmentBookingServiceLocal {

    class BookingItemDto implements Serializable {
        private String sku;
        private String description;
        private int quantity;
        private double weightKg;
        private double declaredValue;

        public BookingItemDto() {}

        public BookingItemDto(String sku, String description, int quantity, double weightKg, double declaredValue) {
            this.sku = sku;
            this.description = description;
            this.quantity = quantity;
            this.weightKg = weightKg;
            this.declaredValue = declaredValue;
        }

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getWeightKg() { return weightKg; }
        public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
        public double getDeclaredValue() { return declaredValue; }
        public void setDeclaredValue(double declaredValue) { this.declaredValue = declaredValue; }
    }

    class BookingSummary implements Serializable {
        private String senderUsername;
        private String origin;
        private String destination;
        private String carrierCode;
        private String serviceLevel;
        private List<BookingItemDto> items;
        private double totalWeightKg;
        private double totalDeclaredValue;
        private double estimatedCostUSD;

        public BookingSummary() {}

        public BookingSummary(String senderUsername, String origin, String destination, String carrierCode, String serviceLevel, List<BookingItemDto> items, double totalWeightKg, double totalDeclaredValue, double estimatedCostUSD) {
            this.senderUsername = senderUsername;
            this.origin = origin;
            this.destination = destination;
            this.carrierCode = carrierCode;
            this.serviceLevel = serviceLevel;
            this.items = items;
            this.totalWeightKg = totalWeightKg;
            this.totalDeclaredValue = totalDeclaredValue;
            this.estimatedCostUSD = estimatedCostUSD;
        }

        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getCarrierCode() { return carrierCode; }
        public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }
        public String getServiceLevel() { return serviceLevel; }
        public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }
        public List<BookingItemDto> getItems() { return items; }
        public void setItems(List<BookingItemDto> items) { this.items = items; }
        public double getTotalWeightKg() { return totalWeightKg; }
        public void setTotalWeightKg(double totalWeightKg) { this.totalWeightKg = totalWeightKg; }
        public double getTotalDeclaredValue() { return totalDeclaredValue; }
        public void setTotalDeclaredValue(double totalDeclaredValue) { this.totalDeclaredValue = totalDeclaredValue; }
        public double getEstimatedCostUSD() { return estimatedCostUSD; }
        public void setEstimatedCostUSD(double estimatedCostUSD) { this.estimatedCostUSD = estimatedCostUSD; }
    }

    void startBooking(String senderUsername, String origin, String destination);

    void addItem(String sku, String description, int quantity, double weightKg, double declaredValue);

    void removeItem(String sku);

    void selectCarrier(String carrierCode, String serviceLevel);

    BookingSummary getCurrentSummary();

    Shipment confirmBooking();

    void cancelBooking();
}
