package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.SupplyChainMonitoringServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.PerformanceMetricRecord;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlert;

import java.util.List;

@Path("/monitoring")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SupplyChainMonitoringController {

    @EJB
    private SupplyChainMonitoringServiceLocal monitoringService;

    @GET
    @Path("/status")
    public Response getSystemStatus() {
        SupplyChainMonitoringServiceLocal.SystemStatusSummary summary = monitoringService.getSystemStatus();
        return Response.ok(summary).build();
    }

    @GET
    @Path("/alerts")
    public Response getUnacknowledgedAlerts() {
        List<SupplyChainAlert> alerts = monitoringService.getUnacknowledgedAlerts();
        return Response.ok(alerts).build();
    }

    @PUT
    @Path("/alerts/{id}/acknowledge")
    public Response acknowledgeAlert(@PathParam("id") Long id) {
        monitoringService.acknowledgeAlert(id);
        return Response.ok("{\"message\": \"Alert " + id + " acknowledged successfully\"}").build();
    }

    @GET
    @Path("/metrics")
    public Response getMetrics(@QueryParam("limit") @DefaultValue("50") int limit) {
        List<PerformanceMetricRecord> metrics = monitoringService.getRecentPerformanceMetrics(limit);
        return Response.ok(metrics).build();
    }
}
