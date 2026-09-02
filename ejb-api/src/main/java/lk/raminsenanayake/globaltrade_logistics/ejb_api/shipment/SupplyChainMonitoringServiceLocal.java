package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.SystemStatusSummary;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.PerformanceMetricRecord;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlert;

import java.util.List;

@Local
public interface SupplyChainMonitoringServiceLocal {

    SystemStatusSummary getSystemStatus();

    List<SupplyChainAlert> getUnacknowledgedAlerts();

    List<PerformanceMetricRecord> getRecentPerformanceMetrics(int limit);

    void acknowledgeAlert(Long alertId);
}
