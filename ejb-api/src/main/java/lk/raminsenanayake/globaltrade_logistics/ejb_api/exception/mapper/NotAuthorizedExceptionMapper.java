package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    private static final Logger LOGGER = Logger.getLogger(NotAuthorizedExceptionMapper.class.getName());

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        LOGGER.warning("[AUTH] NotAuthorizedException: " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                Response.Status.UNAUTHORIZED.getReasonPhrase(),
                "Full authentication is required to access this resource.",
                Response.Status.UNAUTHORIZED.getStatusCode()
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
