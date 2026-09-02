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
public class RouteOption implements Serializable {
    private String routeId;
    private String transportMode;
    private String carrierCode;
    private double estimatedCostUSD;
    private int estimatedDays;
    private double carbonEmissionKg;
    private double riskScore;
}
