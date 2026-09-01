package lk.raminsenanayake.globaltrade_logistics.web.model;

import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.BatchLogisticsServiceLocal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDispatchRequest {
    private List<BatchLogisticsServiceLocal.BatchDispatchItem> items;
}
