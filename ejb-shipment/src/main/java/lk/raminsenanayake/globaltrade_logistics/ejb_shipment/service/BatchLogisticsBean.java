package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.LogisticsBatchProcessingException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.BatchLogisticsServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.RegulatoryComplianceInterceptor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentItem;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
@Interceptors({AuditLoggingInterceptor.class, RegulatoryComplianceInterceptor.class, PerformanceMonitoringInterceptor.class})
public class BatchLogisticsBean implements BatchLogisticsServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(BatchLogisticsBean.class.getName());

    @Inject
    private ShipmentPersistenceService shipmentService;

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public BatchDispatchResult processBatchDispatch(List<BatchDispatchItem> items) {
        if (items == null || items.isEmpty()) {
            throw new LogisticsBatchProcessingException("Batch dispatch items list cannot be empty");
        }

        List<String> trackingNumbers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < items.size(); i++) {
            BatchDispatchItem item = items.get(i);
            try {
                if (item.getDestination() == null || item.getDestination().trim().isEmpty()) {
                    throw new LogisticsBatchProcessingException("Item " + i + ": Destination is required");
                }

                String trackingNumber = "GTL-BCH-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

                Shipment shipment = new Shipment();
                shipment.setTrackingNumber(trackingNumber);
                shipment.setSenderUsername(item.getSenderUsername() != null ? item.getSenderUsername() : "BATCH_SYSTEM");
                shipment.setOriginCountry(item.getOrigin() != null ? item.getOrigin() : "USA");
                shipment.setDestinationCountry(item.getDestination());
                shipment.setCarrierName(item.getCarrier() != null ? item.getCarrier() : "DHL-EXPRESS");
                shipment.setWeightKg(item.getWeightKg());
                shipment.setDeclaredValueUSD(item.getDeclaredValue());
                shipment.setStatus(ShipmentStatus.IN_TRANSIT);
                shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(5));

                if (item.getItemSku() != null) {
                    ShipmentItem sItem = new ShipmentItem();
                    sItem.setShipment(shipment);
                    sItem.setItemSku(item.getItemSku());
                    sItem.setItemName("Batch Item: " + item.getItemSku());
                    sItem.setQuantity(item.getItemQty() > 0 ? item.getItemQty() : 1);
                    sItem.setUnitPrice(item.getDeclaredValue());
                    sItem.setWeightKg(item.getWeightKg());
                    shipment.getItems().add(sItem);
                }

                shipmentService.save(shipment);
                trackingNumbers.add(trackingNumber);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errors.add("Error processing item " + i + ": " + e.getMessage());
                LOGGER.warning("Batch item " + i + " failed: " + e.getMessage());
            }
        }

        LOGGER.info("Processed batch dispatch: Total=" + items.size() + ", Succeeded=" + successCount + ", Failed=" + failureCount);
        return new BatchDispatchResult(items.size(), successCount, failureCount, trackingNumbers, errors);
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public String generateConsolidatedManifest(List<String> trackingNumbers) {
        if (trackingNumbers == null || trackingNumbers.isEmpty()) {
            throw new LogisticsBatchProcessingException("Tracking numbers required for manifest generation");
        }

        StringBuilder manifest = new StringBuilder();
        manifest.append("====================================================\n");
        manifest.append("      GLOBALTRADE LOGISTICS - CONSOLIDATED MANIFEST\n");
        manifest.append("      Generated: ").append(LocalDateTime.now()).append("\n");
        manifest.append("====================================================\n\n");

        double totalWeight = 0;
        double totalValue = 0;
        int count = 0;

        for (String tn : trackingNumbers) {
            var opt = shipmentService.findByTrackingNumber(tn);
            if (opt.isPresent()) {
                Shipment s = opt.get();
                count++;
                totalWeight += s.getWeightKg();
                totalValue += s.getDeclaredValueUSD();
                manifest.append(String.format(" [%02d] Tracking: %-22s | From: %-4s -> To: %-4s | Carrier: %-12s | Status: %-12s | Weight: %6.1f kg | Value: $%8.2f\n",
                        count, s.getTrackingNumber(), s.getOriginCountry(), s.getDestinationCountry(),
                        s.getCarrierName(), s.getStatus(), s.getWeightKg(), s.getDeclaredValueUSD()));
            } else {
                manifest.append(String.format(" [!!] Tracking: %-22s | NOT FOUND IN SYSTEM\n", tn));
            }
        }

        manifest.append("\n----------------------------------------------------\n");
        manifest.append(String.format(" TOTAL SHIPMENTS : %d\n", count));
        manifest.append(String.format(" TOTAL WEIGHT    : %.2f kg\n", totalWeight));
        manifest.append(String.format(" TOTAL VALUE     : $%.2f USD\n", totalValue));
        manifest.append("====================================================\n");

        return manifest.toString();
    }
}
