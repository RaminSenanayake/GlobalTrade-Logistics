package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.PerformanceMetricRecord;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.PerformanceMetricPersistenceService;

import java.util.List;

@ApplicationScoped
public class PerformanceMetricPersistenceServiceImpl implements PerformanceMetricPersistenceService {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void recordMetric(String operationName, long executionTimeMs, String caller, boolean success) {
        PerformanceMetricRecord record = new PerformanceMetricRecord(operationName, executionTimeMs, caller, success);
        em.persist(record);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PerformanceMetricRecord> findRecentMetrics(int limit) {
        return em.createQuery("SELECT p FROM PerformanceMetricRecord p ORDER BY p.timestamp DESC", PerformanceMetricRecord.class)
                .setMaxResults(limit > 0 ? limit : 50)
                .getResultList();
    }
}
