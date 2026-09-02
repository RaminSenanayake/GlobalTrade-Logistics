package lk.raminsenanayake.globaltrade_logistics.ejb_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto implements Serializable {
    private String sku;
    private int quantity;
    private double unitPrice;
    private double weightKg;
}
