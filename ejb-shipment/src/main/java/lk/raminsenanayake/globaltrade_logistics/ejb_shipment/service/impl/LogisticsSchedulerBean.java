package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service.impl;

import jakarta.annotation.security.RunAs;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.LogisticsSchedulerServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclaration;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Inventory;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertSeverity;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertType;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.CustomsPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.InventoryPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
@RunAs("ADMIN")
public class LogisticsSchedulerBean implements LogisticsSchedulerServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(LogisticsSchedulerBean.class.getName());

    @Inject
    private ShipmentPersistenceService shipmentService;

    @Inject
    private CustomsPersistenceService customsService;

    @Inject
    private InventoryPersistenceService inventoryService;

    @Inject
    private AlertPersistenceService alertService;

    @Override
    @Schedule(hour = "*", minute = "*/15", persistent = false)
    public void runScheduledDelayDetection() {
        LOGGER.info("[SCHEDULER] Running periodic delay detection check...");
        List<Shipment> delayedList = shipmentService.findPotentialDelays(LocalDateTime.now());
        for (Shipment s : delayedList) {
            if (s.getStatus() != ShipmentStatus.DELAYED) {
                shipmentService.updateStatus(s.getTrackingNumber(), ShipmentStatus.DELAYED);
                alertService.recordAlert(
                        SupplyChainAlertType.SHIPMENT_DELAY,
                        SupplyChainAlertSeverity.MEDIUM,
                        "Shipment Past ETA",
                        "Shipment " + s.getTrackingNumber() + " has passed its estimated delivery date (" + s.getEstimatedDelivery() + ")",
                        s.getTrackingNumber()
                );
            }
        }
    }

    @Override
    @Schedule(hour = "*", minute = "*/30", persistent = false)
    public void runScheduledCustomsDeadlineCheck() {
        LOGGER.info("[SCHEDULER] Checking for approaching customs filing deadlines...");
        LocalDateTime deadlineThreshold = LocalDateTime.now().plusHours(24);
        List<CustomsDeclaration> approaching = customsService.findApproachingDeadlines(deadlineThreshold);
        for (CustomsDeclaration d : approaching) {
            alertService.recordAlert(
                    SupplyChainAlertType.CUSTOMS_DEADLINE_APPROACHING,
                    SupplyChainAlertSeverity.HIGH,
                    "Customs Deadline Approaching",
                    "Customs declaration " + d.getDeclarationNumber() + " deadline: " + d.getFilingDeadline(),
                    d.getTrackingNumber()
            );
        }
    }

    @Override
    @Schedule(persistent = false)
    public void runScheduledInventoryRestockCheck() {
        LOGGER.info("[SCHEDULER] Running midnight inventory restock audit...");
        List<Inventory> lowStock = inventoryService.findBelowReorderThreshold();
        for (Inventory inv : lowStock) {
            alertService.recordAlert(
                    SupplyChainAlertType.INVENTORY_SHORTAGE,
                    SupplyChainAlertSeverity.MEDIUM,
                    "Inventory Below Safety Threshold",
                    "SKU: " + inv.getSku() + " (" + inv.getName() + ") qty=" + inv.getQty() + ", threshold=" + inv.getReorderThreshold(),
                    inv.getSku()
            );
        }
    }
}
