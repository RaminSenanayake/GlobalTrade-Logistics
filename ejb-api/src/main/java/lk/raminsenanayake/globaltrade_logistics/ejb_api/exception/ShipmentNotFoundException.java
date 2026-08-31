package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ShipmentNotFoundException extends RuntimeException {
    public ShipmentNotFoundException(String message) {
        super(message);
    }
}
