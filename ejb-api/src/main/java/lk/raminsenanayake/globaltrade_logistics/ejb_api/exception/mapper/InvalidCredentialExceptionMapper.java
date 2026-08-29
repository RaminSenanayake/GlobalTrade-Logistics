package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.InvalidCredentialException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

@Provider
public class InvalidCredentialExceptionMapper implements ExceptionMapper<InvalidCredentialException> {
    @Override
    public Response toResponse(InvalidCredentialException exception) {
        exception.printStackTrace();

        ErrorResponse response = ErrorResponse.of(
                Response.Status.UNAUTHORIZED.getReasonPhrase(),
                exception.getMessage(),
                Response.Status.UNAUTHORIZED.getStatusCode()
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(response).build();
    }
}
