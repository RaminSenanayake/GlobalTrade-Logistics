package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.OrderFulfillmentServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.ShipmentTrackingServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentItem;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.web.model.CreateShipmentRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.UpdateShipmentStatusRequest;

import java.util.ArrayList;
import java.util.List;

@Path("/shipments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShipmentController {

    @EJB
    private ShipmentTrackingServiceLocal shipmentTrackingService;

    @EJB
    private OrderFulfillmentServiceLocal orderFulfillmentService;

    @POST
    public Response createShipment(CreateShipmentRequest request) {
        Shipment shipment = new Shipment();
        shipment.setSenderUsername(request.getSenderUsername());
        shipment.setOriginCountry(request.getOriginCountry());
        shipment.setDestinationCountry(request.getDestinationCountry());
        shipment.setOriginHub(request.getOriginHub());
        shipment.setDestinationHub(request.getDestinationHub());
        shipment.setCarrierName(request.getCarrierName());
        shipment.setHazardous(request.isHazardous());
        shipment.setWeightKg(request.getWeightKg());
        shipment.setDeclaredValueUSD(request.getDeclaredValueUSD());
        shipment.setAssignedVendor(request.getAssignedVendor());

        List<ShipmentItem> items = new ArrayList<>();
        if (request.getItems() != null) {
            for (CreateShipmentRequest.ShipmentItemDto itemReq : request.getItems()) {
                ShipmentItem item = new ShipmentItem();
                item.setItemSku(itemReq.getItemSku());
                item.setItemName(itemReq.getItemName());
                item.setQuantity(itemReq.getQuantity());
                item.setUnitPrice(itemReq.getUnitPrice());
                item.setWeightKg(itemReq.getWeightKg());
                items.add(item);
            }
        }

        Shipment created = shipmentTrackingService.createShipment(shipment, items);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{trackingNumber}")
    public Response getShipment(@PathParam("trackingNumber") String trackingNumber) {
        Shipment shipment = shipmentTrackingService.getShipmentByTrackingNumber(trackingNumber);
        return Response.ok(shipment).build();
    }

    @GET
    @Path("/user/{username}")
    public Response getShipmentsBySender(@PathParam("username") String username) {
        List<Shipment> shipments = shipmentTrackingService.getShipmentsBySender(username);
        return Response.ok(shipments).build();
    }

    @GET
    public Response getAllShipments() {
        List<Shipment> shipments = shipmentTrackingService.getAllShipments();
        return Response.ok(shipments).build();
    }

    @PUT
    @Path("/{trackingNumber}/status")
    public Response updateStatus(@PathParam("trackingNumber") String trackingNumber, UpdateShipmentStatusRequest request) {
        ShipmentStatus status = ShipmentStatus.valueOf(request.getStatus().toUpperCase());
        shipmentTrackingService.updateShipmentStatus(trackingNumber, status, request.getUpdatedBy());
        return Response.ok("{\"message\": \"Status updated successfully\"}").build();
    }

    @GET
    @Path("/delays")
    public Response getPotentialDelays() {
        List<Shipment> delays = shipmentTrackingService.detectPotentialDelays();
        return Response.ok(delays).build();
    }
}
