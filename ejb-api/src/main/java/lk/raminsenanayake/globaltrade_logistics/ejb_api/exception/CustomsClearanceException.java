package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CustomsClearanceException extends RuntimeException {
    public CustomsClearanceException(String message) {
        super(message);
    }
}
