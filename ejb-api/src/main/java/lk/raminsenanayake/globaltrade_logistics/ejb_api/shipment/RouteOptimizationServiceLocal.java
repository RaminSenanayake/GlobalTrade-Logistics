package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;

import java.io.Serializable;
import java.util.List;

@Local
public interface RouteOptimizationServiceLocal {

    class RouteOption implements Serializable {
        private String routeId;
        private String transportMode;
        private String carrierCode;
        private double estimatedCostUSD;
        private int estimatedDays;
        private double carbonEmissionKg;
        private double riskScore;

        public RouteOption() {}

        public RouteOption(String routeId, String transportMode, String carrierCode, double estimatedCostUSD, int estimatedDays, double carbonEmissionKg, double riskScore) {
            this.routeId = routeId;
            this.transportMode = transportMode;
            this.carrierCode = carrierCode;
            this.estimatedCostUSD = estimatedCostUSD;
            this.estimatedDays = estimatedDays;
            this.carbonEmissionKg = carbonEmissionKg;
            this.riskScore = riskScore;
        }

        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
        public String getTransportMode() { return transportMode; }
        public void setTransportMode(String transportMode) { this.transportMode = transportMode; }
        public String getCarrierCode() { return carrierCode; }
        public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }
        public double getEstimatedCostUSD() { return estimatedCostUSD; }
        public void setEstimatedCostUSD(double estimatedCostUSD) { this.estimatedCostUSD = estimatedCostUSD; }
        public int getEstimatedDays() { return estimatedDays; }
        public void setEstimatedDays(int estimatedDays) { this.estimatedDays = estimatedDays; }
        public double getCarbonEmissionKg() { return carbonEmissionKg; }
        public void setCarbonEmissionKg(double carbonEmissionKg) { this.carbonEmissionKg = carbonEmissionKg; }
        public double getRiskScore() { return riskScore; }
        public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    }

    class RouteResult implements Serializable {
        private String origin;
        private String destination;
        private double weightKg;
        private RouteOption optimalRoute;
        private List<RouteOption> alternativeRoutes;

        public RouteResult() {}

        public RouteResult(String origin, String destination, double weightKg, RouteOption optimalRoute, List<RouteOption> alternativeRoutes) {
            this.origin = origin;
            this.destination = destination;
            this.weightKg = weightKg;
            this.optimalRoute = optimalRoute;
            this.alternativeRoutes = alternativeRoutes;
        }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public double getWeightKg() { return weightKg; }
        public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
        public RouteOption getOptimalRoute() { return optimalRoute; }
        public void setOptimalRoute(RouteOption optimalRoute) { this.optimalRoute = optimalRoute; }
        public List<RouteOption> getAlternativeRoutes() { return alternativeRoutes; }
        public void setAlternativeRoutes(List<RouteOption> alternativeRoutes) { this.alternativeRoutes = alternativeRoutes; }
    }

    RouteResult calculateOptimalRoute(String origin, String destination, double weightKg, String priority);

    List<RouteOption> compareRoutes(String origin, String destination, double weightKg);
}
