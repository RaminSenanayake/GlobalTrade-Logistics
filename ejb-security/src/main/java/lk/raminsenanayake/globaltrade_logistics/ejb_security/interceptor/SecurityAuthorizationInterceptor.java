package lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Logger;

public class SecurityAuthorizationInterceptor implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SecurityAuthorizationInterceptor.class.getName());

    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object verifySecurityContext(InvocationContext context) throws Exception {
        if (sessionContext != null) {
            try {
                Principal principal = sessionContext.getCallerPrincipal();
                if (principal == null || "ANONYMOUS".equalsIgnoreCase(principal.getName())) {
                    LOGGER.fine("Execution under unauthenticated / background context for " + context.getMethod().getName());
                }
            } catch (EJBAccessException e) {
                LOGGER.warning("Unauthorized access to " + context.getMethod().getName());
                throw e;
            } catch (Exception ignored) {
            }
        }
        return context.proceed();
    }
}
