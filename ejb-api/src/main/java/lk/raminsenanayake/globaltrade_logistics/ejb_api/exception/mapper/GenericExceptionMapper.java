package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.mapper;

import jakarta.ejb.EJBAccessException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.TradeComplianceViolationException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.VendorComplianceException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.ShipmentNotFoundException;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto.ErrorResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable t) {
        Throwable root = findRootCause(t);

        if (root instanceof TradeComplianceViolationException tcve) {
            LOGGER.warning("[UNWRAPPED_TRADE_SANCTION] " + tcve.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorResponse.of("Trade Compliance Violation", tcve.getMessage(), Response.Status.FORBIDDEN.getStatusCode()))
                    .build();
        }

        if (root instanceof EJBAccessException || root instanceof SecurityException) {
            LOGGER.warning("[UNWRAPPED_ACCESS_DENIED] " + root.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorResponse.of("Forbidden", "Access denied: Insufficient privileges to perform this operation.", Response.Status.FORBIDDEN.getStatusCode()))
                    .build();
        }

        if (root instanceof VendorComplianceException vce) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorResponse.of("Vendor Compliance Error", vce.getMessage(), Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        }

        if (root instanceof ShipmentNotFoundException snfe) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorResponse.of("Not Found", snfe.getMessage(), Response.Status.NOT_FOUND.getStatusCode()))
                    .build();
        }

        LOGGER.log(Level.SEVERE, "Unhandled server error: " + t.getMessage(), t);

        ErrorResponse response = ErrorResponse.of(
                Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                root.getMessage() != null ? root.getMessage() : "An internal server error occurred.",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }

    private Throwable findRootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            if (current instanceof TradeComplianceViolationException
                    || current instanceof EJBAccessException
                    || current instanceof SecurityException
                    || current instanceof VendorComplianceException
                    || current instanceof ShipmentNotFoundException) {
                return current;
            }
            current = current.getCause();
        }
        return current;
    }
}
