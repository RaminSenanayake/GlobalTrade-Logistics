package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.PerformanceMetricRecord;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.PerformanceMetricPersistenceService;

import java.util.List;

@Stateless
public class PerformanceMetricPersistenceServiceImpl implements PerformanceMetricPersistenceService {

    @PersistenceContext(unitName = "globalTrade-logistics")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void recordMetric(String operationName, long executionTimeMs, String caller, boolean success) {
        PerformanceMetricRecord record = new PerformanceMetricRecord(operationName, executionTimeMs, caller, success);
        em.persist(record);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<PerformanceMetricRecord> findRecentMetrics(int limit) {
        return em.createQuery("SELECT p FROM PerformanceMetricRecord p ORDER BY p.timestamp DESC", PerformanceMetricRecord.class)
                .setMaxResults(limit > 0 ? limit : 50)
                .getResultList();
    }
}
