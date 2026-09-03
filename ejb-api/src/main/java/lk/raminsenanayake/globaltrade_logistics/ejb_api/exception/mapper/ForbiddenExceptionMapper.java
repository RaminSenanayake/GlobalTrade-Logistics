package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    private static final Logger LOGGER = Logger.getLogger(ForbiddenExceptionMapper.class.getName());

    @Override
    public Response toResponse(ForbiddenException exception) {
        LOGGER.warning("[FORBIDDEN] ForbiddenException: " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                Response.Status.FORBIDDEN.getReasonPhrase(),
                "Access denied: You do not have permission to access this resource.",
                Response.Status.FORBIDDEN.getStatusCode()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
