package lk.raminsenanayake.globaltrade_logistics.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectCarrierRequest {
    private String carrierCode;
    private String serviceLevel;
}
