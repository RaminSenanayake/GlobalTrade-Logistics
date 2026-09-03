package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.CustomsClearanceException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class CustomsClearanceExceptionMapper implements ExceptionMapper<CustomsClearanceException> {

    private static final Logger LOGGER = Logger.getLogger(CustomsClearanceExceptionMapper.class.getName());

    @Override
    public Response toResponse(CustomsClearanceException exception) {
        LOGGER.warning("[CUSTOMS_ERROR] " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                "Customs Clearance Error",
                exception.getMessage(),
                Response.Status.BAD_REQUEST.getStatusCode()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
