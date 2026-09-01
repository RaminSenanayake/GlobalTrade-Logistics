package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service.impl;

import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.BatchLogisticsServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchLogisticsBeanTest {

    @Mock
    private ShipmentPersistenceService shipmentService;

    @InjectMocks
    private BatchLogisticsBean bean;

    @Test
    void processBatchDispatch_Success() {
        when(shipmentService.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));

        BatchLogisticsServiceLocal.BatchDispatchItem item1 = new BatchLogisticsServiceLocal.BatchDispatchItem(
                "USA", "GBR", "USER1", "CARRIER1", 5.0, 100.0, "SKU1", 2
        );
        BatchLogisticsServiceLocal.BatchDispatchItem item2 = new BatchLogisticsServiceLocal.BatchDispatchItem(
                "USA", "FRA", "USER2", "CARRIER2", 3.0, 50.0, "SKU2", 1
        );

        BatchLogisticsServiceLocal.BatchDispatchResult result = bean.processBatchDispatch(List.of(item1, item2));

        assertNotNull(result);
        assertEquals(2, result.getTotalProcessed());
        assertEquals(2, result.getTotalSucceeded());
        assertEquals(0, result.getTotalFailed());
        assertEquals(2, result.getGeneratedTrackingNumbers().size());
    }
}
