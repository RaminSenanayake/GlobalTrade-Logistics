package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TradeComplianceViolationException extends RuntimeException {
    public TradeComplianceViolationException(String message) {
        super(message);
    }
}
