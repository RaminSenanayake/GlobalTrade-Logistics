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
@Table(name = "customs_declarations", indexes = {
        @Index(name = "idx_customs_dec_number", columnList = "declaration_number", unique = true),
        @Index(name = "idx_customs_tracking", columnList = "tracking_number"),
        @Index(name = "idx_customs_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomsDeclaration implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "declaration_number", nullable = false, unique = true, length = 64)
    private String declarationNumber;

    @Column(name = "tracking_number", nullable = false, length = 64)
    private String trackingNumber;

    @Column(name = "origin_country", nullable = false, length = 3)
    private String originCountry;

    @Column(name = "destination_country", nullable = false, length = 3)
    private String destinationCountry;

    @Column(name = "cargo_description", length = 500)
    private String cargoDescription;

    @Column(name = "declared_value_usd", nullable = false)
    private double declaredValueUSD;

    @Column(name = "tariff_code", length = 32)
    private String tariffCode;

    @Column(name = "duty_fee_usd")
    private double dutyFeeUSD;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private CustomsDeclarationStatus status;

    @Column(name = "filing_deadline")
    private LocalDateTime filingDeadline;

    @Column(name = "clearance_date")
    private LocalDateTime clearanceDate;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "compliance_notes", length = 1000)
    private String complianceNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
