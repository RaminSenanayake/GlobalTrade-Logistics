package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOGGER = Logger.getLogger(WebApplicationExceptionMapper.class.getName());

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response response = exception.getResponse();
        int statusCode = response != null ? response.getStatus() : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String reasonPhrase = Response.Status.fromStatusCode(statusCode) != null
                ? Response.Status.fromStatusCode(statusCode).getReasonPhrase()
                : "HTTP Error";

        LOGGER.warning("[HTTP_ERROR] Status " + statusCode + ": " + exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                reasonPhrase,
                exception.getMessage() != null ? exception.getMessage() : reasonPhrase,
                statusCode
        );

        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }
}
