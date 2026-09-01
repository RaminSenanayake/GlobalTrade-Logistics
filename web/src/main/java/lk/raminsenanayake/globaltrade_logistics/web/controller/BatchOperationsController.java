package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.BatchLogisticsServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.web.model.BatchDispatchRequest;

import java.util.List;

@Path("/batch")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BatchOperationsController {

    @EJB
    private BatchLogisticsServiceLocal batchService;

    @POST
    @Path("/dispatch")
    public Response processBatchDispatch(BatchDispatchRequest request) {
        BatchLogisticsServiceLocal.BatchDispatchResult result = batchService.processBatchDispatch(request.getItems());
        return Response.ok(result).build();
    }

    @POST
    @Path("/manifest")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateManifest(List<String> trackingNumbers) {
        String manifest = batchService.generateConsolidatedManifest(trackingNumbers);
        return Response.ok(manifest).build();
    }
}
