package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {

    private static final Logger LOGGER = Logger.getLogger(SecurityExceptionMapper.class.getName());

    @Override
    public Response toResponse(SecurityException exception) {
        LOGGER.warning("[SECURITY] SecurityException caught: " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                Response.Status.FORBIDDEN.getReasonPhrase(),
                exception.getMessage() != null ? exception.getMessage() : "Access denied: Insufficient privileges.",
                Response.Status.FORBIDDEN.getStatusCode()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
