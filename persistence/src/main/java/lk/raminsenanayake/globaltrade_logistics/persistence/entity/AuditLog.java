package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_performer", columnList = "performed_by"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String action;

    @Column(name = "entity_name", length = 64)
    private String entityName;

    @Column(name = "entity_id", length = 64)
    private String entityId;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Column(name = "caller_role", length = 64)
    private String callerRole;

    @Column(length = 2000)
    private String details;

    @Column(nullable = false, length = 32)
    private String status; // SUCCESS or FAILURE

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "duration_ms")
    private long durationMs;

    public AuditLog(String action, String entityName, String entityId, String performedBy, String callerRole, String details, String status, long durationMs) {
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.callerRole = callerRole;
        this.details = details;
        this.status = status;
        this.durationMs = durationMs;
        this.timestamp = LocalDateTime.now();
    }
}
