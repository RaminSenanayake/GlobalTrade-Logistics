package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;

import java.io.Serializable;
import java.util.List;

@Local
public interface OrderFulfillmentServiceLocal {

    class OrderItemDto implements Serializable {
        private String sku;
        private int quantity;
        private double unitPrice;
        private double weightKg;

        public OrderItemDto() {}

        public OrderItemDto(String sku, int quantity, double unitPrice, double weightKg) {
            this.sku = sku;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.weightKg = weightKg;
        }

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        public double getWeightKg() { return weightKg; }
        public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    }

    Shipment fulfillOrder(String orderReference, String destinationCountry, List<OrderItemDto> items, String preferredCarrier);
}
