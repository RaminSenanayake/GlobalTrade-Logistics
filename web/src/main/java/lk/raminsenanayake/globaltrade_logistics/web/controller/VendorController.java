package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.VendorScorecard;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.vendor.VendorEvaluationServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Vendor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;
import lk.raminsenanayake.globaltrade_logistics.web.model.AssignVendorRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.CreateVendorRequest;

import java.util.List;
import java.util.Map;

@Path("/vendors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VendorController {

    @EJB
    private VendorEvaluationServiceLocal vendorService;

    @POST
    public Response registerVendor(CreateVendorRequest request) {
        Vendor vendor = new Vendor();
        vendor.setName(request.getName());
        vendor.setCountry(request.getCountry());
        vendor.setContactEmail(request.getContactEmail());

        Vendor saved = vendorService.registerVendor(vendor);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/{vendorCode}/evaluate")
    public Response evaluateVendor(@PathParam("vendorCode") String vendorCode) {
        VendorScorecard scorecard = vendorService.evaluateVendor(vendorCode);
        return Response.ok(scorecard).build();
    }

    @GET
    @Path("/{vendorCode}/scorecard")
    public Response getScorecard(@PathParam("vendorCode") String vendorCode) {
        VendorScorecard scorecard = vendorService.getVendorScorecard(vendorCode);
        return Response.ok(scorecard).build();
    }

    @GET
    public Response getAllVendors(@QueryParam("status") String status) {
        if (status != null && !status.trim().isEmpty()) {
            VendorComplianceStatus vStatus = VendorComplianceStatus.valueOf(status.toUpperCase());
            List<Vendor> list = vendorService.getVendorsByStatus(vStatus);
            return Response.ok(list).build();
        }
        List<Vendor> list = vendorService.getAllVendors();
        return Response.ok(list).build();
    }

    @POST
    @Path("/assign")
    public Response assignVendor(AssignVendorRequest request) {
        vendorService.assignVendorToShipment(request.getTrackingNumber(), request.getVendorCode());
        return Response.ok(Map.of("message", "Vendor assigned successfully")).build();
    }
}
