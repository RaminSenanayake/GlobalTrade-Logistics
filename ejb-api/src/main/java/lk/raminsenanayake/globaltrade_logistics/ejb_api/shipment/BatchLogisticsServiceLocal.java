package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;

import java.io.Serializable;
import java.util.List;

@Local
public interface BatchLogisticsServiceLocal {

    class BatchDispatchItem implements Serializable {
        private String origin;
        private String destination;
        private String senderUsername;
        private String carrier;
        private double weightKg;
        private double declaredValue;
        private String itemSku;
        private int itemQty;

        public BatchDispatchItem() {}

        public BatchDispatchItem(String origin, String destination, String senderUsername, String carrier, double weightKg, double declaredValue, String itemSku, int itemQty) {
            this.origin = origin;
            this.destination = destination;
            this.senderUsername = senderUsername;
            this.carrier = carrier;
            this.weightKg = weightKg;
            this.declaredValue = declaredValue;
            this.itemSku = itemSku;
            this.itemQty = itemQty;
        }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
        public String getCarrier() { return carrier; }
        public void setCarrier(String carrier) { this.carrier = carrier; }
        public double getWeightKg() { return weightKg; }
        public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
        public double getDeclaredValue() { return declaredValue; }
        public void setDeclaredValue(double declaredValue) { this.declaredValue = declaredValue; }
        public String getItemSku() { return itemSku; }
        public void setItemSku(String itemSku) { this.itemSku = itemSku; }
        public int getItemQty() { return itemQty; }
        public void setItemQty(int itemQty) { this.itemQty = itemQty; }
    }

    class BatchDispatchResult implements Serializable {
        private int totalProcessed;
        private int totalSucceeded;
        private int totalFailed;
        private List<String> generatedTrackingNumbers;
        private List<String> errors;

        public BatchDispatchResult() {}

        public BatchDispatchResult(int totalProcessed, int totalSucceeded, int totalFailed, List<String> generatedTrackingNumbers, List<String> errors) {
            this.totalProcessed = totalProcessed;
            this.totalSucceeded = totalSucceeded;
            this.totalFailed = totalFailed;
            this.generatedTrackingNumbers = generatedTrackingNumbers;
            this.errors = errors;
        }

        public int getTotalProcessed() { return totalProcessed; }
        public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
        public int getTotalSucceeded() { return totalSucceeded; }
        public void setTotalSucceeded(int totalSucceeded) { this.totalSucceeded = totalSucceeded; }
        public int getTotalFailed() { return totalFailed; }
        public void setTotalFailed(int totalFailed) { this.totalFailed = totalFailed; }
        public List<String> getGeneratedTrackingNumbers() { return generatedTrackingNumbers; }
        public void setGeneratedTrackingNumbers(List<String> generatedTrackingNumbers) { this.generatedTrackingNumbers = generatedTrackingNumbers; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }

    BatchDispatchResult processBatchDispatch(List<BatchDispatchItem> items);

    String generateConsolidatedManifest(List<String> trackingNumbers);
}
