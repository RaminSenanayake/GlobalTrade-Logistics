package lk.raminsenanayake.globaltrade_logistics.ejb_api.vendor;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Vendor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;

import java.io.Serializable;
import java.util.List;

@Local
public interface VendorEvaluationServiceLocal {

    class VendorScorecard implements Serializable {
        private String vendorCode;
        private String name;
        private double performanceRating;
        private double onTimeDeliveryRate;
        private int totalShipments;
        private int delayedShipments;
        private VendorComplianceStatus complianceStatus;
        private String recommendation;

        public VendorScorecard() {}

        public VendorScorecard(String vendorCode, String name, double performanceRating, double onTimeDeliveryRate, int totalShipments, int delayedShipments, VendorComplianceStatus complianceStatus, String recommendation) {
            this.vendorCode = vendorCode;
            this.name = name;
            this.performanceRating = performanceRating;
            this.onTimeDeliveryRate = onTimeDeliveryRate;
            this.totalShipments = totalShipments;
            this.delayedShipments = delayedShipments;
            this.complianceStatus = complianceStatus;
            this.recommendation = recommendation;
        }

        public String getVendorCode() { return vendorCode; }
        public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPerformanceRating() { return performanceRating; }
        public void setPerformanceRating(double performanceRating) { this.performanceRating = performanceRating; }
        public double getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
        public void setOnTimeDeliveryRate(double onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
        public int getTotalShipments() { return totalShipments; }
        public void setTotalShipments(int totalShipments) { this.totalShipments = totalShipments; }
        public int getDelayedShipments() { return delayedShipments; }
        public void setDelayedShipments(int delayedShipments) { this.delayedShipments = delayedShipments; }
        public VendorComplianceStatus getComplianceStatus() { return complianceStatus; }
        public void setComplianceStatus(VendorComplianceStatus complianceStatus) { this.complianceStatus = complianceStatus; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }

    Vendor registerVendor(Vendor vendor);

    VendorScorecard evaluateVendor(String vendorCode);

    VendorScorecard getVendorScorecard(String vendorCode);

    List<Vendor> getAllVendors();

    List<Vendor> getVendorsByStatus(VendorComplianceStatus status);

    void assignVendorToShipment(String trackingNumber, String vendorCode);
}
