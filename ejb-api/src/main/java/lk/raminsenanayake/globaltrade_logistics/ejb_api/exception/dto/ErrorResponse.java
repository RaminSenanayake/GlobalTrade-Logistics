package lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String error,
        String message,
        int status,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(String error,String message, int status) {
        return new ErrorResponse(error, message, status, LocalDateTime.now());
    }
}
