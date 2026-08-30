package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendors", indexes = {
        @Index(name = "idx_vendor_code", columnList = "vendor_code", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_code", nullable = false, unique = true, length = 32)
    private String vendorCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country", nullable = false, length = 3)
    private String country;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "performance_rating")
    private double performanceRating; // Scale 0.0 - 5.0

    @Column(name = "on_time_delivery_rate")
    private double onTimeDeliveryRate; // Percentage 0 - 100

    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_status", nullable = false, length = 32)
    private VendorComplianceStatus complianceStatus;

    @Column(name = "total_shipments_handled")
    private int totalShipmentsHandled;

    @Column(name = "delayed_shipments_count")
    private int delayedShipmentsCount;

    @Column(name = "last_evaluated_at")
    private LocalDateTime lastEvaluatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Vendor(String vendorCode, String name, String country, String contactEmail) {
        this.vendorCode = vendorCode;
        this.name = name;
        this.country = country;
        this.contactEmail = contactEmail;
        this.performanceRating = 5.0;
        this.onTimeDeliveryRate = 100.0;
        this.complianceStatus = VendorComplianceStatus.COMPLIANT;
        this.totalShipmentsHandled = 0;
        this.delayedShipmentsCount = 0;
        this.lastEvaluatedAt = LocalDateTime.now();
    }
}
