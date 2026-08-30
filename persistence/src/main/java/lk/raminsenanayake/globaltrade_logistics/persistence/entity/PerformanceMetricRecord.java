package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_metrics", indexes = {
        @Index(name = "idx_perf_op", columnList = "operation_name"),
        @Index(name = "idx_perf_time", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetricRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_name", nullable = false, length = 128)
    private String operationName;

    @Column(name = "execution_time_ms", nullable = false)
    private long executionTimeMs;

    @Column(length = 64)
    private String caller;

    @Column(nullable = false)
    private boolean success;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public PerformanceMetricRecord(String operationName, long executionTimeMs, String caller, boolean success) {
        this.operationName = operationName;
        this.executionTimeMs = executionTimeMs;
        this.caller = caller;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }
}
