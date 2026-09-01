package lk.raminsenanayake.globaltrade_logistics.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignVendorRequest {
    private String trackingNumber;
    private String vendorCode;
}
