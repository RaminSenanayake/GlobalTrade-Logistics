package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.RouteOptimizationServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.AuditLoggingInterceptor;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor.PerformanceMonitoringInterceptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@Interceptors({AuditLoggingInterceptor.class, PerformanceMonitoringInterceptor.class})
public class RouteOptimizationBean implements RouteOptimizationServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(RouteOptimizationBean.class.getName());

    @Override
    @PermitAll
    public RouteResult calculateOptimalRoute(String origin, String destination, double weightKg, String priority) {
        List<RouteOption> options = compareRoutes(origin, destination, weightKg);

        RouteOption optimal;
        if ("SPEED".equalsIgnoreCase(priority)) {
            optimal = options.stream().min(Comparator.comparingInt(RouteOption::getEstimatedDays)).orElse(options.get(0));
        } else if ("EMISSION".equalsIgnoreCase(priority) || "ECO".equalsIgnoreCase(priority)) {
            optimal = options.stream().min(Comparator.comparingDouble(RouteOption::getCarbonEmissionKg)).orElse(options.get(0));
        } else if ("RELIABILITY".equalsIgnoreCase(priority)) {
            optimal = options.stream().min(Comparator.comparingDouble(RouteOption::getRiskScore)).orElse(options.get(0));
        } else {
            // Default: COST
            optimal = options.stream().min(Comparator.comparingDouble(RouteOption::getEstimatedCostUSD)).orElse(options.get(0));
        }

        List<RouteOption> alternatives = options.stream()
                .filter(o -> !o.getRouteId().equals(optimal.getRouteId()))
                .toList();

        LOGGER.info(String.format("Calculated optimal route for %s -> %s (Weight: %.1fkg, Priority: %s): %s via %s ($%.2f, %dd)",
                origin, destination, weightKg, priority, optimal.getCarrierCode(), optimal.getTransportMode(),
                optimal.getEstimatedCostUSD(), optimal.getEstimatedDays()));

        return new RouteResult(origin, destination, weightKg, optimal, alternatives);
    }

    @Override
    @PermitAll
    public List<RouteOption> compareRoutes(String origin, String destination, double weightKg) {
        List<RouteOption> options = new ArrayList<>();

        double effectiveWeight = Math.max(1.0, weightKg);

        // Air Freight Express
        options.add(new RouteOption(
                "RT-AIR-01",
                "AIR",
                "DHL-EXPRESS",
                25.0 + (effectiveWeight * 12.5),
                2,
                effectiveWeight * 4.5,
                0.05
        ));

        // Air Cargo Standard
        options.add(new RouteOption(
                "RT-AIR-02",
                "AIR",
                "FEDEX-CARGO",
                20.0 + (effectiveWeight * 9.5),
                4,
                effectiveWeight * 3.8,
                0.08
        ));

        // Ocean Freight
        options.add(new RouteOption(
                "RT-SEA-01",
                "SEA",
                "MAERSK-OCEAN",
                15.0 + (effectiveWeight * 1.2),
                14,
                effectiveWeight * 0.4,
                0.15
        ));

        // Rail Freight
        options.add(new RouteOption(
                "RT-RAIL-01",
                "RAIL",
                "EURASIA-RAIL",
                18.0 + (effectiveWeight * 3.5),
                8,
                effectiveWeight * 1.1,
                0.10
        ));

        return options;
    }
}
