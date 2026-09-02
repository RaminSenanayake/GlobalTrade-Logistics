package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.customs.CustomsComplianceServiceLocal;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclaration;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclarationStatus;
import lk.raminsenanayake.globaltrade_logistics.web.model.CustomsFilingRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.CustomsReviewRequest;

import java.util.List;
import java.util.Map;

@Path("/customs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomsController {

    @EJB
    private CustomsComplianceServiceLocal customsService;

    @POST
    @Path("/declarations")
    public Response submitDeclaration(CustomsFilingRequest request) {
        CustomsDeclaration declaration = new CustomsDeclaration();
        declaration.setTrackingNumber(request.getTrackingNumber());
        declaration.setOriginCountry(request.getOriginCountry() != null ? request.getOriginCountry() : "USA");
        declaration.setDestinationCountry(request.getDestinationCountry());
        declaration.setCargoDescription(request.getCargoDescription());
        declaration.setDeclaredValueUSD(request.getDeclaredValueUSD());
        declaration.setTariffCode(request.getTariffCode());
        declaration.setDutyFeeUSD(request.getDutyFeeUSD());

        CustomsDeclaration saved = customsService.submitDeclaration(declaration);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @Path("/declarations/{declarationNumber}/review")
    public Response reviewDeclaration(@PathParam("declarationNumber") String declarationNumber, CustomsReviewRequest request) {
        CustomsDeclarationStatus status = CustomsDeclarationStatus.valueOf(request.getStatus().toUpperCase());
        customsService.reviewDeclaration(declarationNumber, status, request.getReviewedBy(), request.getNotes());
        return Response.ok(
                Map.of("message", "Customs declaration review recorded successfully")
        ).build();
    }

    @GET
    @Path("/compliance/{trackingNumber}")
    public Response checkCompliance(@PathParam("trackingNumber") String trackingNumber) {
        boolean compliant = customsService.checkCompliance(trackingNumber);
        return Response.ok(
                Map.of(
                        "trackingNumber", trackingNumber,
                        "compliant", compliant
                )
        ).build();
    }

    @GET
    @Path("/declarations/pending")
    public Response getPendingDeclarations() {
        List<CustomsDeclaration> list = customsService.getPendingDeclarations();
        return Response.ok(list).build();
    }

    @GET
    @Path("/declarations/deadlines")
    public Response getApproachingDeadlines(@QueryParam("hours") @DefaultValue("24") int hours) {
        List<CustomsDeclaration> list = customsService.getApproachingDeadlineDeclarations(hours);
        return Response.ok(list).build();
    }
}
