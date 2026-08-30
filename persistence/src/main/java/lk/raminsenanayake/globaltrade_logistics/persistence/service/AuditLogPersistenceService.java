package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import lk.raminsenanayake.globaltrade_logistics.persistence.entity.AuditLog;

import java.util.List;

public interface AuditLogPersistenceService {
    void logAudit(String action, String entityName, String entityId, String performedBy, String callerRole, String details, String status, long durationMs);
    List<AuditLog> findAll(int maxResults);
    List<AuditLog> findByPerformer(String performedBy);
    List<AuditLog> findByAction(String action);
}
