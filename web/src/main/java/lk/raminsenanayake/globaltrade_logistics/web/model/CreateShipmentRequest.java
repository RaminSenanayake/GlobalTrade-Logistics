package lk.raminsenanayake.globaltrade_logistics.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {
    private String senderUsername;
    private String originCountry;
    private String destinationCountry;
    private String originHub;
    private String destinationHub;
    private String carrierName;
    private boolean hazardous;
    private double weightKg;
    private double declaredValueUSD;
    private String assignedVendor;
    private List<ShipmentItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipmentItemDto {
        private String itemSku;
        private String itemName;
        private int quantity;
        private double unitPrice;
        private double weightKg;
    }
}
