package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.LogisticsBatchProcessingException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class LogisticsBatchProcessingExceptionMapper implements ExceptionMapper<LogisticsBatchProcessingException> {

    private static final Logger LOGGER = Logger.getLogger(LogisticsBatchProcessingExceptionMapper.class.getName());

    @Override
    public Response toResponse(LogisticsBatchProcessingException exception) {
        LOGGER.warning("[BATCH_ERROR] " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                "Batch Logistics Error",
                exception.getMessage(),
                Response.Status.BAD_REQUEST.getStatusCode()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
