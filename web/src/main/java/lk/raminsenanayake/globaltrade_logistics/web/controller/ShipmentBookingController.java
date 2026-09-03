package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.ShipmentBookingServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.web.model.AddBookingItemRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.SelectCarrierRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.StartBookingRequest;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;

@Path("/booking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShipmentBookingController {

    private static final String BOOKING_SESSION_ATTR = "SHIPMENT_BOOKING_SESSION";

    private ShipmentBookingServiceLocal getBookingService(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        ShipmentBookingServiceLocal bookingBean = (ShipmentBookingServiceLocal) session.getAttribute(BOOKING_SESSION_ATTR);
        if (bookingBean == null) {
            try {
                InitialContext ctx = new InitialContext();
                // Lookup stateful session bean for client
                bookingBean = (ShipmentBookingServiceLocal) ctx.lookup("java:module/ShipmentBookingSessionBean!lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.ShipmentBookingServiceLocal");
                session.setAttribute(BOOKING_SESSION_ATTR, bookingBean);
            } catch (NamingException e) {
                try {
                    InitialContext ctx = new InitialContext();
                    bookingBean = (ShipmentBookingServiceLocal) ctx.lookup("java:global/globaltrade-logistics-ear/lk.raminsenanayake.globaltrade-logistics-ejb-shipment-1.0/ShipmentBookingSessionBean!lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment.ShipmentBookingServiceLocal");
                    session.setAttribute(BOOKING_SESSION_ATTR, bookingBean);
                } catch (Exception ex) {
                    throw new RuntimeException("Could not initialize Stateful Booking Session Bean: " + ex.getMessage(), ex);
                }
            }
        }
        return bookingBean;
    }

    @POST
    @Path("/start")
    public Response startBooking(StartBookingRequest request, @Context HttpServletRequest req) {
        ShipmentBookingServiceLocal service = getBookingService(req);
        service.startBooking(request.getSenderUsername(), request.getOrigin(), request.getDestination());
        return Response.ok(Map.of("message", "Booking session initiated for " + request.getSenderUsername())).build();
    }

    @POST
    @Path("/items")
    public Response addItem(AddBookingItemRequest request, @Context HttpServletRequest req) {
        ShipmentBookingServiceLocal service = getBookingService(req);
        service.addItem(request.getSku(), request.getDescription(), request.getQuantity(), request.getWeightKg(), request.getDeclaredValue());
        return Response.ok(Map.of("message", "Item added to booking session: " + request.getSku())).build();
    }

    @DELETE
    @Path("/items/{sku}")
    public Response removeItem(@PathParam("sku") String sku, @Context HttpServletRequest req) {
        ShipmentBookingServiceLocal service = getBookingService(req);
        service.removeItem(sku);
        return Response.ok(Map.of("message", "Item removed: " + sku)).build();
    }

    @POST
    @Path("/carrier")
    public Response selectCarrier(SelectCarrierRequest request, @Context HttpServletRequest req) {
        ShipmentBookingServiceLocal service = getBookingService(req);
        service.selectCarrier(request.getCarrierCode(), request.getServiceLevel());
        return Response.ok(Map.of("message", "Carrier selected: " + request.getCarrierCode())).build();
    }

    @GET
    @Path("/summary")
    public Response getSummary(@Context HttpServletRequest req) {
        ShipmentBookingServiceLocal service = getBookingService(req);
        return Response.ok(service.getCurrentSummary()).build();
    }

    @POST
    @Path("/confirm")
    public Response confirmBooking(@Context HttpServletRequest req) {
        ShipmentBookingServiceLocal service = getBookingService(req);
        Shipment shipment = service.confirmBooking();
        req.getSession().removeAttribute(BOOKING_SESSION_ATTR);
        return Response.ok(shipment).build();
    }

    @POST
    @Path("/cancel")
    public Response cancelBooking(@Context HttpServletRequest req) {
        ShipmentBookingServiceLocal service = getBookingService(req);
        service.cancelBooking();
        req.getSession().removeAttribute(BOOKING_SESSION_ATTR);
        return Response.ok(Map.of("message", "Booking session canceled successfully")).build();
    }
}
