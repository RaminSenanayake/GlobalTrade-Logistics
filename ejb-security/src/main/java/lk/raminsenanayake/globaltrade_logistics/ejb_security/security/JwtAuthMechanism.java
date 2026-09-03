package lk.raminsenanayake.globaltrade_logistics.ejb_security.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.raminsenanayake.globaltrade_logistics.ejb_security.util.JwtUtil;

import java.util.Set;

@ApplicationScoped
public class JwtAuthMechanism implements HttpAuthenticationMechanism {

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpMessageContext context) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (JwtUtil.isValid(token)) {
                DecodedJWT jwt = JwtUtil.parseToken(token);

                String username = jwt.getSubject();
                String role = jwt.getClaim("role").asString();
                Set<String> roles = role != null ? Set.of(role) : Set.of();
                return context.notifyContainerAboutLogin(username, roles);
            }
        }

        if (context.isProtected()) {
            return context.responseUnauthorized();
        }

        return context.doNothing();
    }
}
