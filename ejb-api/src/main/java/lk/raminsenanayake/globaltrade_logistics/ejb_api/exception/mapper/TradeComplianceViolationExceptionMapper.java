package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.TradeComplianceViolationException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class TradeComplianceViolationExceptionMapper implements ExceptionMapper<TradeComplianceViolationException> {

    private static final Logger LOGGER = Logger.getLogger(TradeComplianceViolationExceptionMapper.class.getName());

    @Override
    public Response toResponse(TradeComplianceViolationException exception) {
        LOGGER.warning("[TRADE_SANCTION] Blocked transaction: " + exception.getMessage());

        ErrorResponse response = ErrorResponse.of(
                "Trade Compliance Violation",
                exception.getMessage(),
                Response.Status.FORBIDDEN.getStatusCode()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
