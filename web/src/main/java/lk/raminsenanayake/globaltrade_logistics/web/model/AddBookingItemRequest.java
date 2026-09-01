package lk.raminsenanayake.globaltrade_logistics.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingItemRequest {
    private String sku;
    private String description;
    private int quantity;
    private double weightKg;
    private double declaredValue;
}
