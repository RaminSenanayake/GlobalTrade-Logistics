package lk.raminsenanayake.globaltrade_logistics.ejb_shipment.service.impl;

import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.RouteOptimizationServiceLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteOptimizationBeanTest {

    private RouteOptimizationBean bean;

    @BeforeEach
    void setUp() {
        bean = new RouteOptimizationBean();
    }

    @Test
    void calculateOptimalRoute_CostPriority_SelectsCheapest() {
        RouteOptimizationServiceLocal.RouteResult result = bean.calculateOptimalRoute("USA", "GBR", 20.0, "COST");

        assertNotNull(result);
        assertEquals("USA", result.getOrigin());
        assertEquals("GBR", result.getDestination());
        assertNotNull(result.getOptimalRoute());
        // Maersk Sea is cheapest for 20kg
        assertEquals("MAERSK-OCEAN", result.getOptimalRoute().getCarrierCode());
        assertEquals(3, result.getAlternativeRoutes().size());
    }

    @Test
    void calculateOptimalRoute_SpeedPriority_SelectsFastest() {
        RouteOptimizationServiceLocal.RouteResult result = bean.calculateOptimalRoute("USA", "DEU", 15.0, "SPEED");

        assertNotNull(result);
        assertEquals("DHL-EXPRESS", result.getOptimalRoute().getCarrierCode());
        assertEquals(2, result.getOptimalRoute().getEstimatedDays());
    }

    @Test
    void compareRoutes_ReturnsAllAvailableOptions() {
        List<RouteOptimizationServiceLocal.RouteOption> options = bean.compareRoutes("USA", "JPN", 50.0);
        assertNotNull(options);
        assertEquals(4, options.size());
    }
}
