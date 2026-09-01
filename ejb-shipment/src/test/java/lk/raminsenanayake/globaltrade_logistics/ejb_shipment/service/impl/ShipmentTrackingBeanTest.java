package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service.impl;

import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.ShipmentNotFoundException;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentTrackingBeanTest {

    @Mock
    private ShipmentPersistenceService shipmentService;

    @Mock
    private AlertPersistenceService alertService;

    @InjectMocks
    private ShipmentTrackingBean bean;

    @Test
    void createShipment_Success() {
        Shipment s = new Shipment();
        s.setOriginCountry("USA");
        s.setDestinationCountry("GBR");

        when(shipmentService.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));

        Shipment created = bean.createShipment(s, new ArrayList<>());
        assertNotNull(created);
        assertNotNull(created.getTrackingNumber());
        assertEquals(ShipmentStatus.CREATED, created.getStatus());
    }

    @Test
    void getShipmentByTrackingNumber_NotFound_ThrowsException() {
        when(shipmentService.findByTrackingNumber("INVALID")).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFoundException.class, () -> bean.getShipmentByTrackingNumber("INVALID"));
    }

    @Test
    void updateShipmentStatus_Success() {
        Shipment s = new Shipment();
        s.setTrackingNumber("TRK-999");
        when(shipmentService.findByTrackingNumber("TRK-999")).thenReturn(Optional.of(s));

        bean.updateShipmentStatus("TRK-999", ShipmentStatus.IN_TRANSIT, "ADMIN_USER");
        verify(shipmentService).updateStatus("TRK-999", ShipmentStatus.IN_TRANSIT);
    }
}
