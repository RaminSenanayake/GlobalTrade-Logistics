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
public class BatchDispatchItem implements Serializable {
    private String origin;
    private String destination;
    private String senderUsername;
    private String carrier;
    private double weightKg;
    private double declaredValue;
    private String itemSku;
    private int itemQty;
}
