package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.TradeComplianceViolationException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.ShipmentBookingServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentItem;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Stateful
@Interceptors({AuditLoggingInterceptor.class, PerformanceMonitoringInterceptor.class})
public class ShipmentBookingSessionBean implements ShipmentBookingServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(ShipmentBookingSessionBean.class.getName());

    @Inject
    private ShipmentPersistenceService shipmentService;

    private String senderUsername;
    private String origin;
    private String destination;
    private String carrierCode = "DHL-EXPRESS";
    private String serviceLevel = "EXPRESS";
    private final List<BookingItemDto> items = new ArrayList<>();

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL"})
    public void startBooking(String senderUsername, String origin, String destination) {
        this.senderUsername = senderUsername;
        this.origin = origin;
        this.destination = destination;
        this.items.clear();
        LOGGER.info("Started shipment booking session for " + senderUsername + " (" + origin + " -> " + destination + ")");
    }

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL"})
    public void addItem(String sku, String description, int quantity, double weightKg, double declaredValue) {
        items.removeIf(i -> i.getSku().equalsIgnoreCase(sku));
        items.add(new BookingItemDto(sku, description, quantity, weightKg, declaredValue));
        LOGGER.info("Added item to booking session: " + sku + " (Qty: " + quantity + ")");
    }

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL"})
    public void removeItem(String sku) {
        items.removeIf(i -> i.getSku().equalsIgnoreCase(sku));
        LOGGER.info("Removed item from booking session: " + sku);
    }

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL"})
    public void selectCarrier(String carrierCode, String serviceLevel) {
        this.carrierCode = carrierCode;
        this.serviceLevel = serviceLevel;
        LOGGER.info("Carrier chosen: " + carrierCode + " (" + serviceLevel + ")");
    }

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL"})
    public BookingSummary getCurrentSummary() {
        double totalWeight = items.stream().mapToDouble(i -> i.getWeightKg() * i.getQuantity()).sum();
        double totalDeclaredValue = items.stream().mapToDouble(i -> i.getDeclaredValue() * i.getQuantity()).sum();
        double estimatedCost = 25.0 + (totalWeight * 8.5);

        return new BookingSummary(
                senderUsername, origin, destination,
                carrierCode, serviceLevel, new ArrayList<>(items),
                totalWeight, totalDeclaredValue, estimatedCost
        );
    }

    @Override
    @Remove
    @RolesAllowed({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL"})
    public Shipment confirmBooking() {
        if (items.isEmpty()) {
            throw new TradeComplianceViolationException("Cannot confirm booking without items");
        }

        String trackingNumber = "GTL-BKG-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        double totalWeight = items.stream().mapToDouble(i -> i.getWeightKg() * i.getQuantity()).sum();
        double totalDeclaredValue = items.stream().mapToDouble(i -> i.getDeclaredValue() * i.getQuantity()).sum();

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber);
        shipment.setSenderUsername(senderUsername != null ? senderUsername : "CUSTOMER");
        shipment.setOriginCountry(origin != null ? origin : "USA");
        shipment.setDestinationCountry(destination != null ? destination : "GBR");
        shipment.setCarrierName(carrierCode);
        shipment.setWeightKg(totalWeight);
        shipment.setDeclaredValueUSD(totalDeclaredValue);
        shipment.setStatus(ShipmentStatus.CREATED);
        shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(5));

        for (BookingItemDto bItem : items) {
            ShipmentItem sItem = new ShipmentItem();
            sItem.setShipment(shipment);
            sItem.setItemSku(bItem.getSku());
            sItem.setItemName(bItem.getDescription());
            sItem.setQuantity(bItem.getQuantity());
            sItem.setUnitPrice(bItem.getDeclaredValue());
            sItem.setWeightKg(bItem.getWeightKg());
            shipment.getItems().add(sItem);
        }

        Shipment saved = shipmentService.save(shipment);
        LOGGER.info("Booking session confirmed into Shipment: " + trackingNumber);
        return saved;
    }

    @Override
    @Remove
    @RolesAllowed({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL"})
    public void cancelBooking() {
        LOGGER.info("Booking session canceled for " + senderUsername);
        items.clear();
    }
}
