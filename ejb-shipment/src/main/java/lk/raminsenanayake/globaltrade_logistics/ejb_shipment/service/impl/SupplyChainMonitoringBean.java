package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.SupplyChainMonitoringServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.*;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.*;

import java.util.List;
import java.util.logging.Logger;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Interceptors({AuditLoggingInterceptor.class, PerformanceMonitoringInterceptor.class})
public class SupplyChainMonitoringBean implements SupplyChainMonitoringServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(SupplyChainMonitoringBean.class.getName());

    @Inject
    private DataInitializerService dataInitializerService;

    @Inject
    private ShipmentPersistenceService shipmentService;

    @Inject
    private CustomsPersistenceService customsService;

    @Inject
    private InventoryPersistenceService inventoryService;

    @Inject
    private AlertPersistenceService alertService;

    @Inject
    private PerformanceMetricPersistenceService metricService;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init() {
        timerService.createSingleActionTimer(100L, new TimerConfig("startup-seed", false));
    }

    @Timeout
    public void onStartupTimer(Timer timer) {
        dataInitializerService.initializeDefaultData();
    }

    @Override
    @Lock(LockType.READ)
    @PermitAll
    public SystemStatusSummary getSystemStatus() {
        List<Shipment> allShipments = shipmentService.findAll();
        List<Shipment> activeShipments = allShipments.stream()
                .filter(s -> s.getStatus() != ShipmentStatus.DELIVERED && s.getStatus() != ShipmentStatus.CANCELLED)
                .toList();

        int totalActive = activeShipments.size();
        int delayed = (int) activeShipments.stream().filter(s -> s.getStatus() == ShipmentStatus.DELAYED).count();

        List<CustomsDeclaration> pendingCustoms = customsService.findByStatus(CustomsDeclarationStatus.SUBMITTED);
        int customsPendingCount = pendingCustoms.size();

        List<SupplyChainAlert> unackAlerts = alertService.findUnacknowledged();
        int unackAlertCount = unackAlerts.size();

        List<Inventory> allInv = inventoryService.findAll();
        int lowStockCount = (int) allInv.stream().filter(i -> i.getQty() <= i.getReorderThreshold()).count();

        List<PerformanceMetricRecord> recent = metricService.findRecentMetrics(50);
        double avgExecTime = recent.isEmpty() ? 0.0 :
                recent.stream().mapToLong(PerformanceMetricRecord::getExecutionTimeMs).average().orElse(0.0);

        String health = "HEALTHY";
        if (unackAlertCount > 10 || delayed > 5) {
            health = "CRITICAL";
        } else if (unackAlertCount > 0 || delayed > 0 || lowStockCount > 0) {
            health = "DEGRADED";
        }

        return new SystemStatusSummary(
                totalActive, delayed, customsPendingCount, unackAlertCount,
                lowStockCount, avgExecTime, health
        );
    }

    @Override
    @Lock(LockType.READ)
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL", "CUSTOM_OFFICIAL"})
    public List<SupplyChainAlert> getUnacknowledgedAlerts() {
        return alertService.findUnacknowledged();
    }

    @Override
    @Lock(LockType.READ)
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public List<PerformanceMetricRecord> getRecentPerformanceMetrics(int limit) {
        return metricService.findRecentMetrics(limit > 0 ? limit : 50);
    }

    @Override
    @Lock(LockType.WRITE)
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL", "CUSTOM_OFFICIAL"})
    public void acknowledgeAlert(Long alertId) {
        alertService.acknowledgeAlert(alertId);
        LOGGER.info("Acknowledged alert ID: " + alertId);
    }
}
