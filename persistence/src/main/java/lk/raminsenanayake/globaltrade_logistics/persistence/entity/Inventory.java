package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "sku", unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(nullable = false)
    private int qty;

    @Column(name = "reorder_threshold")
    private int reorderThreshold;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "warehouse_location")
    private String warehouseLocation;

    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;

    @Version
    private Long version;

    public Inventory(String sku, String name, String category, int qty, int reorderThreshold, double unitPrice, String warehouseLocation) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.qty = qty;
        this.reorderThreshold = reorderThreshold;
        this.unitPrice = unitPrice;
        this.warehouseLocation = warehouseLocation;
        this.lastRestocked = LocalDateTime.now();
    }
}
