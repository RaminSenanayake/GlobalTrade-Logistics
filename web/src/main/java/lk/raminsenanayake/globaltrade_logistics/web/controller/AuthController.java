package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.service.LoginService;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.User;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.UserRole;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.UserPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.web.model.LoginRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.RefreshRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.RegisterUserRequest;

import java.util.List;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    private LoginService loginService;

    @Inject
    private UserPersistenceService userPersistenceService;

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
                    .entity(Map.of("error", "Missing refresh token"))
                    .build();
        }
        return loginService.refreshAccessToken(refreshRequest.refreshToken());
    }

    @POST
    @RolesAllowed("ADMIN")
    @Path("/register")
    public Response registerUser(RegisterUserRequest req) {
        if (req == null || req.getUsername() == null || req.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Username and password are required."))
                    .build();
        }

        if (userPersistenceService.existsByUsername(req.getUsername())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Username already exists: " + req.getUsername()))
                    .build();
        }

        UserRole role = req.getRole() != null ? UserRole.valueOf(req.getRole().toUpperCase()) : UserRole.CUSTOMER;
        User user = userPersistenceService.createUser(req.getUsername(), req.getPassword(), role);

        return Response.status(Response.Status.CREATED).entity(Map.of(
                "message", "User registered successfully",
                "username", user.getUsername(),
                "role", user.getRole().toString()
        )).build();
    }

    @GET
    @Path("/users")
    @RolesAllowed("ADMIN")
    public Response listUsers() {
        List<User> users = userPersistenceService.getAllUsers();
        return Response.ok(users).build();
    }
}
