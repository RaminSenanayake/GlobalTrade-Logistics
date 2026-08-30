package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.AuditLog;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AuditLogPersistenceService;

import java.util.List;

@ApplicationScoped
public class AuditLogPersistenceServiceImpl implements AuditLogPersistenceService {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void logAudit(String action, String entityName, String entityId, String performedBy, String callerRole, String details, String status, long durationMs) {
        AuditLog auditLog = new AuditLog(action, entityName, entityId, performedBy, callerRole, details, status, durationMs);
        em.persist(auditLog);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AuditLog> findAll(int maxResults) {
        return em.createQuery("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC", AuditLog.class)
                .setMaxResults(maxResults > 0 ? maxResults : 100)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AuditLog> findByPerformer(String performedBy) {
        return em.createQuery("SELECT a FROM AuditLog a WHERE a.performedBy = :performer ORDER BY a.timestamp DESC", AuditLog.class)
                .setParameter("performer", performedBy)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AuditLog> findByAction(String action) {
        return em.createQuery("SELECT a FROM AuditLog a WHERE a.action = :action ORDER BY a.timestamp DESC", AuditLog.class)
                .setParameter("action", action)
                .getResultList();
    }
}
