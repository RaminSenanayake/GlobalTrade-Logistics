package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "supply_chain_alerts", indexes = {
        @Index(name = "idx_alert_type", columnList = "alert_type"),
        @Index(name = "idx_alert_severity", columnList = "severity"),
        @Index(name = "idx_alert_acknowledged", columnList = "acknowledged")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyChainAlert  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 32)
    private SupplyChainAlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 16)
    private SupplyChainAlertSeverity severity;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String message;

    @Column(name = "reference_code", length = 64)
    private String referenceCode;

    @Column(nullable = false)
    private boolean acknowledged;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public SupplyChainAlert(SupplyChainAlertType alertType, SupplyChainAlertSeverity severity, String title, String message, String referenceCode) {
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.referenceCode = referenceCode;
        this.acknowledged = false;
        this.createdAt = LocalDateTime.now();
    }
}
