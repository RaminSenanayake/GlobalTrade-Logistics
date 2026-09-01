package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.PerformanceMetricRecord;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlert;

import java.io.Serializable;
import java.util.List;

@Local
public interface SupplyChainMonitoringServiceLocal {

    class SystemStatusSummary implements Serializable {
        private int activeShipmentsCount;
        private int delayedShipmentsCount;
        private int pendingCustomsDeclarationsCount;
        private int unacknowledgedAlertsCount;
        private int lowStockInventoryCount;
        private double averageExecutionTimeMs;
        private String systemHealthStatus;

        public SystemStatusSummary() {}

        public SystemStatusSummary(int activeShipmentsCount, int delayedShipmentsCount, int pendingCustomsDeclarationsCount, int unacknowledgedAlertsCount, int lowStockInventoryCount, double averageExecutionTimeMs, String systemHealthStatus) {
            this.activeShipmentsCount = activeShipmentsCount;
            this.delayedShipmentsCount = delayedShipmentsCount;
            this.pendingCustomsDeclarationsCount = pendingCustomsDeclarationsCount;
            this.unacknowledgedAlertsCount = unacknowledgedAlertsCount;
            this.lowStockInventoryCount = lowStockInventoryCount;
            this.averageExecutionTimeMs = averageExecutionTimeMs;
            this.systemHealthStatus = systemHealthStatus;
        }

        public int getActiveShipmentsCount() { return activeShipmentsCount; }
        public void setActiveShipmentsCount(int activeShipmentsCount) { this.activeShipmentsCount = activeShipmentsCount; }
        public int getDelayedShipmentsCount() { return delayedShipmentsCount; }
        public void setDelayedShipmentsCount(int delayedShipmentsCount) { this.delayedShipmentsCount = delayedShipmentsCount; }
        public int getPendingCustomsDeclarationsCount() { return pendingCustomsDeclarationsCount; }
        public void setPendingCustomsDeclarationsCount(int pendingCustomsDeclarationsCount) { this.pendingCustomsDeclarationsCount = pendingCustomsDeclarationsCount; }
        public int getUnacknowledgedAlertsCount() { return unacknowledgedAlertsCount; }
        public void setUnacknowledgedAlertsCount(int unacknowledgedAlertsCount) { this.unacknowledgedAlertsCount = unacknowledgedAlertsCount; }
        public int getLowStockInventoryCount() { return lowStockInventoryCount; }
        public void setLowStockInventoryCount(int lowStockInventoryCount) { this.lowStockInventoryCount = lowStockInventoryCount; }
        public double getAverageExecutionTimeMs() { return averageExecutionTimeMs; }
        public void setAverageExecutionTimeMs(double averageExecutionTimeMs) { this.averageExecutionTimeMs = averageExecutionTimeMs; }
        public String getSystemHealthStatus() { return systemHealthStatus; }
        public void setSystemHealthStatus(String systemHealthStatus) { this.systemHealthStatus = systemHealthStatus; }
    }

    SystemStatusSummary getSystemStatus();

    List<SupplyChainAlert> getUnacknowledgedAlerts();

    List<PerformanceMetricRecord> getRecentPerformanceMetrics(int limit);

    void acknowledgeAlert(Long alertId);
}
