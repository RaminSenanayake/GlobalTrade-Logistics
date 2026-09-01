package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service.impl;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.InsufficientInventoryException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.OrderFulfillmentServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.RegulatoryComplianceInterceptor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.*;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.InventoryPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
@Interceptors({AuditLoggingInterceptor.class, RegulatoryComplianceInterceptor.class, PerformanceMonitoringInterceptor.class})
public class OrderFulfillmentBean implements OrderFulfillmentServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(OrderFulfillmentBean.class.getName());

    @Inject
    private InventoryPersistenceService inventoryService;

    @Inject
    private ShipmentPersistenceService shipmentService;

    @Inject
    private AlertPersistenceService alertService;

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public Shipment fulfillOrder(String orderReference, String destinationCountry, List<OrderItemDto> items, String preferredCarrier) {
        if (items == null || items.isEmpty()) {
            throw new InsufficientInventoryException("Cannot fulfill empty order");
        }

        // Validate inventory for every item before deducting
        for (OrderItemDto item : items) {
            Optional<Inventory> optInv = inventoryService.findBySku(item.getSku());
            if (optInv.isEmpty()) {
                throw new InsufficientInventoryException("Inventory SKU not found: " + item.getSku());
            }
            Inventory inv = optInv.get();
            if (inv.getQty() < item.getQuantity()) {
                throw new InsufficientInventoryException("Insufficient stock for SKU " + item.getSku() + ": requested " + item.getQuantity() + ", available " + inv.getQty());
            }
        }

        // Deduct inventory and calculate totals
        double totalWeight = 0;
        double totalValue = 0;
        for (OrderItemDto item : items) {
            inventoryService.deductStock(item.getSku(), item.getQuantity());
            totalWeight += (item.getWeightKg() * item.getQuantity());
            totalValue += (item.getUnitPrice() * item.getQuantity());

            // Check if restock alert is needed
            Optional<Inventory> updated = inventoryService.findBySku(item.getSku());
            if (updated.isPresent() && updated.get().getQty() <= updated.get().getReorderThreshold()) {
                if (alertService != null) {
                    alertService.recordAlert(
                            SupplyChainAlertType.INVENTORY_SHORTAGE,
                            SupplyChainAlertSeverity.MEDIUM,
                            "Low Stock Warning: " + item.getSku(),
                            "Stock reached " + updated.get().getQty() + " (Threshold: " + updated.get().getReorderThreshold() + ")",
                            item.getSku()
                    );
                }
            }
        }

        String trackingNumber = "GTL-ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber);
        shipment.setSenderUsername("FULFILLMENT_SYSTEM");
        shipment.setOriginCountry("USA");
        shipment.setDestinationCountry(destinationCountry);
        shipment.setCarrierName(preferredCarrier != null ? preferredCarrier : "DHL-EXPRESS");
        shipment.setWeightKg(totalWeight);
        shipment.setDeclaredValueUSD(totalValue);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(4));

        for (OrderItemDto item : items) {
            ShipmentItem sItem = new ShipmentItem();
            sItem.setShipment(shipment);
            sItem.setItemSku(item.getSku());
            sItem.setItemName("Order Item: " + item.getSku());
            sItem.setQuantity(item.getQuantity());
            sItem.setUnitPrice(item.getUnitPrice());
            sItem.setWeightKg(item.getWeightKg());
            shipment.getItems().add(sItem);
        }

        Shipment saved = shipmentService.save(shipment);
        LOGGER.info("Order " + orderReference + " fulfilled into Shipment " + trackingNumber + " with " + items.size() + " items");
        return saved;
    }
}
