package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class LogisticsBatchProcessingException extends RuntimeException {
    public LogisticsBatchProcessingException(String message) {
        super(message);
    }
}
