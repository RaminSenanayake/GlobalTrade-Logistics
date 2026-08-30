package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "shipment_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    @JsonbTransient
    private Shipment shipment;

    @Column(name = "item_sku", nullable = false)
    private String itemSku;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "weight_kg")
    private double weightKg;
}
