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
public class SystemStatusSummary implements Serializable {
    private int activeShipmentsCount;
    private int delayedShipmentsCount;
    private int pendingCustomsDeclarationsCount;
    private int unacknowledgedAlertsCount;
    private int lowStockInventoryCount;
    private double averageExecutionTimeMs;
    private String systemHealthStatus;
}
