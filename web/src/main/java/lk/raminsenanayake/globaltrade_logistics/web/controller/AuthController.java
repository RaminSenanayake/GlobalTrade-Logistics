package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.service.LoginService;
import lk.raminsenanayake.globaltrade_logistics.web.model.LoginRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.RefreshRequest;

import java.util.Map;

@Path("/auth")
public class AuthController {

    @Inject
    private LoginService loginService;

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.username() == null || loginRequest.password() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Login Request is null or empty")).build();
        }
        return loginService.login(loginRequest.username(), loginRequest.password());
    }

    @POST
    @Path("/refresh")
    public Response refreshAccessToken(RefreshRequest refreshRequest) {
        if (refreshRequest == null || refreshRequest.refreshToken() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(
                            Map.of("error", "Missing refresh token")
                    )
                    .build();
        }
        return loginService.refreshAccessToken(refreshRequest.refreshToken());
    }
}
