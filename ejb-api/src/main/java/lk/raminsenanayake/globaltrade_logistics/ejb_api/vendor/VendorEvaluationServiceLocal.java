package lk.raminsenanayake.globaltrade_logistics.ejb_api.vendor;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.VendorScorecard;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Vendor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;

import java.util.List;

@Local
public interface VendorEvaluationServiceLocal {

    Vendor registerVendor(Vendor vendor);

    VendorScorecard evaluateVendor(String vendorCode);

    VendorScorecard getVendorScorecard(String vendorCode);

    List<Vendor> getAllVendors();

    List<Vendor> getVendorsByStatus(VendorComplianceStatus status);

    void assignVendorToShipment(String trackingNumber, String vendorCode);
}
