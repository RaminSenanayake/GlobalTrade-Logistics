package lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AuditLogPersistenceService;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.logging.Logger;

public class AuditLoggingInterceptor implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AuditLoggingInterceptor.class.getName());

    @Inject
    private AuditLogPersistenceService auditLogService;

    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        long startTime = System.currentTimeMillis();
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSimpleName();
        String caller = "ANONYMOUS";
        String callerRole = "SYSTEM";

        try {
            if (sessionContext != null) {
                Principal callerPrincipal = sessionContext.getCallerPrincipal();
                if (callerPrincipal != null && !callerPrincipal.getName().isEmpty()) {
                    caller = callerPrincipal.getName();
                }
                if (sessionContext.isCallerInRole("ADMIN")) {
                    callerRole = "ADMIN";
                } else if (sessionContext.isCallerInRole("LOGISTIC_PERSONNEL")) {
                    callerRole = "LOGISTIC_PERSONNEL";
                } else if (sessionContext.isCallerInRole("CUSTOM_OFFICIAL")) {
                    callerRole = "CUSTOM_OFFICIAL";
                } else if (sessionContext.isCallerInRole("VENDOR")) {
                    callerRole = "VENDOR";
                } else if (sessionContext.isCallerInRole("CUSTOMER")) {
                    callerRole = "CUSTOMER";
                }
            }
        } catch (Exception ignored) {
            // EJB context principal unavailable in unauthenticated/background thread
        }

        String entityId = "N/A";
        Object[] params = context.getParameters();
        if (params != null && params.length > 0 && params[0] != null) {
            entityId = String.valueOf(params[0]);
            if (entityId.length() > 50) {
                entityId = entityId.substring(0, 47) + "...";
            }
        }

        String details = "Invoked " + className + "." + methodName + "(" + Arrays.toString(params) + ")";
        if (details.length() > 255) {
            details = details.substring(0, 252) + "...";
        }

        try {
            Object result = context.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (auditLogService != null) {
                auditLogService.logAudit(methodName, className, entityId, caller, callerRole, details, "SUCCESS", duration);
            }
            LOGGER.info(String.format("[AUDIT] User:%s Role:%s executed %s.%s in %dms [SUCCESS]",
                    caller, callerRole, className, methodName, duration));
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            if (auditLogService != null) {
                auditLogService.logAudit(methodName, className, entityId, caller, callerRole, "Exception: " + ex.getMessage(), "FAILED", duration);
            }
            LOGGER.warning(String.format("[AUDIT] User:%s Role:%s executed %s.%s in %dms [FAILED: %s]",
                    caller, callerRole, className, methodName, duration, ex.getMessage()));
            throw ex;
        }
    }
}
