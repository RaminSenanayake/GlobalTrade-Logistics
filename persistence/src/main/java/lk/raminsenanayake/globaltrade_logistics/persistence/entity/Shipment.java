package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "idx_shipment_tracking", columnList = "tracking_number", unique = true),
        @Index(name = "idx_shipment_status", columnList = "status"),
        @Index(name = "idx_shipment_dest", columnList = "destination_country")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 64)
    private String trackingNumber;

    @Column(name = "sender_username", nullable = false)
    private String senderUsername;

    @Column(name = "assigned_vendor")
    private String assignedVendor;

    @Column(name = "carrier_name", nullable = false)
    private String carrierName;

    @Column(name = "origin_country", nullable = false, length = 3)
    private String originCountry;

    @Column(name = "destination_country", nullable = false, length = 3)
    private String destinationCountry;

    @Column(name = "origin_hub")
    private String originHub;

    @Column(name = "destination_hub")
    private String destinationHub;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ShipmentStatus status;

    @Column(name = "is_hazardous")
    private boolean hazardous;

    @Column(name = "weight_kg")
    private double weightKg;

    @Column(name = "declared_value_usd")
    private double declaredValueUSD;

    @Column(name = "route_plan", length = 1000)
    private String routePlan;

    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @Column(name = "actual_delivery")
    private LocalDateTime actualDelivery;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ShipmentItem> items = new ArrayList<>();

    public void addItem(ShipmentItem item) {
        items.add(item);
        item.setShipment(this);
    }
}
