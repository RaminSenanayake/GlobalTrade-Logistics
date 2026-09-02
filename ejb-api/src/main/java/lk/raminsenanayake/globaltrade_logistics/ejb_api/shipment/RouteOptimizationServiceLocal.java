package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.RouteOption;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.RouteResult;

import java.util.List;

@Local
public interface RouteOptimizationServiceLocal {

    RouteResult calculateOptimalRoute(String origin, String destination, double weightKg, String priority);

    List<RouteOption> compareRoutes(String origin, String destination, double weightKg);
}
