package lk.raminsenanayake.globaltrade_logistics.ejb_api.dto;

import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorScorecard implements Serializable {
    private String vendorCode;
    private String name;
    private double performanceRating;
    private double onTimeDeliveryRate;
    private int totalShipments;
    private int delayedShipments;
    private VendorComplianceStatus complianceStatus;
    private String recommendation;
}
