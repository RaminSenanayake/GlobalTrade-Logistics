package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.BatchDispatchItem;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.BatchDispatchResult;

import java.util.List;

@Local
public interface BatchLogisticsServiceLocal {

    BatchDispatchResult processBatchDispatch(List<BatchDispatchItem> items);

    String generateConsolidatedManifest(List<String> trackingNumbers);
}
