package lk.raminsenanayake.globaltrade_logistics.ejb_security.service.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.InvalidCredentialException;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.service.LoginService;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.util.JwtUtil;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.RefreshToken;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.RefreshTokenPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.UserPersistenceService;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequestScoped
public class LoginServiceImpl implements LoginService {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private RefreshTokenPersistenceService refreshTokenPersistenceService;

    @Inject
    private UserPersistenceService userPersistenceService;

    @Override
    public Response login(String username, String password) {
        CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential(username, password));

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            String token = JwtUtil.generateToken(
                    result.getCallerPrincipal().getName(),
                    result.getCallerGroups()
            );

            RefreshToken refreshToken = refreshTokenPersistenceService.createToken(result.getCallerPrincipal().getName());

            return Response.status(Response.Status.OK).entity(
                    Map.of(
                            "accessToken", token,
                            "refreshToken", refreshToken.getToken(),
                            "username", result.getCallerPrincipal().getName(),
                            "role", result.getCallerGroups().iterator().next()
                    )
            ).build();
        } else {
            throw new InvalidCredentialException("Invalid username or password");
        }
    }

    @Override
    public Response refreshAccessToken(String refreshToken) {
        Optional<RefreshToken> rt = refreshTokenPersistenceService.findValidToken(refreshToken);
        if (rt.isEmpty()) {
            throw new InvalidCredentialException("Invalid or expired refresh token");
        }

        RefreshToken oldToken = rt.get();
        String username = oldToken.getUsername();

        refreshTokenPersistenceService.deleteToken(oldToken.getToken());
        RefreshToken newRefreshToken = refreshTokenPersistenceService.createToken(username);

        String userRole = userPersistenceService.getUser(username).get().getRole().toString();

        String token = JwtUtil.generateToken(username, Set.of(userRole));

        return Response.status(Response.Status.OK)
                .entity(
                        Map.of(
                                "accessToken", token,
                                "refreshToken", newRefreshToken.getToken(),
                                "username", username,
                                "role", userRole
                        )
                ).build();
    }
}
