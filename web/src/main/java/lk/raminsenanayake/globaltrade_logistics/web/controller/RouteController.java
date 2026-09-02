package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.RouteOption;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.RouteResult;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.RouteOptimizationServiceLocal;

import java.util.List;

@Path("/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RouteController {

    @EJB
    private RouteOptimizationServiceLocal routeService;

    @GET
    @Path("/optimize")
    public Response getOptimalRoute(
            @QueryParam("origin") String origin,
            @QueryParam("destination") String destination,
            @QueryParam("weight") double weight,
            @QueryParam("priority") @DefaultValue("COST") String priority) {

        RouteResult result = routeService.calculateOptimalRoute(origin, destination, weight, priority);
        return Response.ok(result).build();
    }

    @GET
    @Path("/compare")
    public Response compareRoutes(
            @QueryParam("origin") String origin,
            @QueryParam("destination") String destination,
            @QueryParam("weight") double weight) {

        List<RouteOption> options = routeService.compareRoutes(origin, destination, weight);
        return Response.ok(options).build();
    }
}
