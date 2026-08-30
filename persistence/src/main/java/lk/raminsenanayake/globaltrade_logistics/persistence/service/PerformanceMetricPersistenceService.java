package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import lk.raminsenanayake.globaltrade_logistics.persistence.entity.PerformanceMetricRecord;

import java.util.List;

public interface PerformanceMetricPersistenceService {
    void recordMetric(String operationName, long executionTimeMs, String caller, boolean success);
    List<PerformanceMetricRecord> findRecentMetrics(int limit);
}
