package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ejb.EJBAccessException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class EJBAccessExceptionMapper implements ExceptionMapper<EJBAccessException> {

    private static final Logger LOGGER = Logger.getLogger(EJBAccessExceptionMapper.class.getName());

    @Override
    public Response toResponse(EJBAccessException exception) {
        LOGGER.warning("[ACCESS_DENIED] EJB Access Exception: " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                Response.Status.FORBIDDEN.getReasonPhrase(),
                "Access denied: You do not have the required role or authority to perform this operation.",
                Response.Status.FORBIDDEN.getStatusCode()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
