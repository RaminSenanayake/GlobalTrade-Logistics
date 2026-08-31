package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
