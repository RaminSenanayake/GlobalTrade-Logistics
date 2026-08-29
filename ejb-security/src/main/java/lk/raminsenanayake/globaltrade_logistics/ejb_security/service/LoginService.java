package lk.raminsenanayake.globaltrade_logistics.ejb_security.service;

import jakarta.ws.rs.core.Response;

public interface LoginService {
    Response login(String username, String password);
    Response refreshAccessToken(String refreshToken);
}
