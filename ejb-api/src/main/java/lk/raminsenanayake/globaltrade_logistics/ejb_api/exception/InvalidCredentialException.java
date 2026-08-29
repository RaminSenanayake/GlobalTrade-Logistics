package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}
