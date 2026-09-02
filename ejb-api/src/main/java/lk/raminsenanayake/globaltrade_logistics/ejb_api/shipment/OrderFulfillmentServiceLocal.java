package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.OrderItemDto;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;

import java.util.List;

@Local
public interface OrderFulfillmentServiceLocal {

    Shipment fulfillOrder(String orderReference, String destinationCountry, List<OrderItemDto> items, String preferredCarrier);
}
