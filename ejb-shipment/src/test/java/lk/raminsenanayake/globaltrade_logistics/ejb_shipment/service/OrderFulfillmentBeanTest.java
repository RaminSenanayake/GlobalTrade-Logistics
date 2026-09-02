package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service;

import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.OrderItemDto;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.InsufficientInventoryException;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Inventory;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.InventoryPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFulfillmentBeanTest {

    @Mock
    private InventoryPersistenceService inventoryService;

    @Mock
    private ShipmentPersistenceService shipmentService;

    @Mock
    private AlertPersistenceService alertService;

    @InjectMocks
    private OrderFulfillmentBean bean;

    @Test
    void fulfillOrder_Success() {
        Inventory inv = new Inventory("SKU-100", "Widget A", "Electronics", 50, 10, 25.0, "WH-1");
        when(inventoryService.findBySku("SKU-100")).thenReturn(Optional.of(inv));
        when(shipmentService.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));

        OrderItemDto item = new OrderItemDto("SKU-100", 5, 25.0, 2.0);
        Shipment shipment = bean.fulfillOrder("ORD-001", "DEU", List.of(item), "DHL-EXPRESS");

        assertNotNull(shipment);
        assertEquals("DEU", shipment.getDestinationCountry());
        verify(inventoryService).deductStock("SKU-100", 5);
    }

    @Test
    void fulfillOrder_InsufficientStock_ThrowsException() {
        Inventory inv = new Inventory("SKU-100", "Widget A", "Electronics", 2, 10, 25.0, "WH-1");
        when(inventoryService.findBySku("SKU-100")).thenReturn(Optional.of(inv));

        OrderItemDto item = new OrderItemDto("SKU-100", 10, 25.0, 2.0);
        assertThrows(InsufficientInventoryException.class, () -> bean.fulfillOrder("ORD-001", "DEU", List.of(item), "DHL-EXPRESS"));
    }
}
