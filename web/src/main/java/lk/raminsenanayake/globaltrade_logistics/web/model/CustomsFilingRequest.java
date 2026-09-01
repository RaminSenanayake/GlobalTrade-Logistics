package lk.raminsenanayake.globaltrade_logistics.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomsFilingRequest {
    private String trackingNumber;
    private String originCountry;
    private String destinationCountry;
    private String cargoDescription;
    private double declaredValueUSD;
    private String tariffCode;
    private double dutyFeeUSD;
}
