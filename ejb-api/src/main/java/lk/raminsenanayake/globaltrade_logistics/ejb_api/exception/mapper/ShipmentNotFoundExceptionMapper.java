package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.ShipmentNotFoundException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class ShipmentNotFoundExceptionMapper implements ExceptionMapper<ShipmentNotFoundException> {

    private static final Logger LOGGER = Logger.getLogger(ShipmentNotFoundExceptionMapper.class.getName());

    @Override
    public Response toResponse(ShipmentNotFoundException exception) {
        LOGGER.warning("[SHIPMENT_NOT_FOUND] " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                Response.Status.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                Response.Status.NOT_FOUND.getStatusCode()
        );

        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
