package lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertSeverity;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertType;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.PerformanceMetricPersistenceService;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Logger;

public class PerformanceMonitoringInterceptor implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PerformanceMonitoringInterceptor.class.getName());
    private static final long SLA_THRESHOLD_MS = 2000;

    @Inject
    private PerformanceMetricPersistenceService metricService;

    @Inject
    private AlertPersistenceService alertService;

    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object monitorPerformance(InvocationContext context) throws Exception {
        long start = System.currentTimeMillis();
        String operation = context.getTarget().getClass().getSimpleName() + "." + context.getMethod().getName();
        String caller = "SYSTEM";

        try {
            if (sessionContext != null) {
                Principal principal = sessionContext.getCallerPrincipal();
                if (principal != null && !principal.getName().isEmpty()) {
                    caller = principal.getName();
                }
            }
        } catch (Exception ignored) {
        }

        boolean success = false;
        try {
            Object result = context.proceed();
            success = true;
            return result;
        } finally {
            long duration = System.currentTimeMillis() - start;

            if (metricService != null) {
                try {
                    metricService.recordMetric(operation, duration, caller, success);
                } catch (Exception e) {
                    LOGGER.warning("Failed to record metric: " + e.getMessage());
                }
            }

            if (duration > SLA_THRESHOLD_MS && alertService != null) {
                try {
                    alertService.recordAlert(
                            SupplyChainAlertType.SECURITY_VIOLATION,
                            SupplyChainAlertSeverity.HIGH,
                            "SLA Violation: Slow Operation",
                            String.format("Operation %s took %d ms (Threshold: %d ms)", operation, duration, SLA_THRESHOLD_MS),
                            operation
                    );
                } catch (Exception e) {
                    LOGGER.warning("Failed to record SLA alert: " + e.getMessage());
                }
            }
        }
    }
}
