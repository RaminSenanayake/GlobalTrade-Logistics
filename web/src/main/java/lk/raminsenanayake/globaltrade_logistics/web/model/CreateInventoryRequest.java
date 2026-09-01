package lk.raminsenanayake.globaltrade_logistics.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryRequest {
    private String sku;
    private String name;
    private String category;
    private int qty;
    private int reorderThreshold;
    private double unitPrice;
    private String warehouseLocation;
}
