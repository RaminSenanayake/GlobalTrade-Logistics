package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VendorComplianceException extends RuntimeException {
    public VendorComplianceException(String message) {
        super(message);
    }
}
