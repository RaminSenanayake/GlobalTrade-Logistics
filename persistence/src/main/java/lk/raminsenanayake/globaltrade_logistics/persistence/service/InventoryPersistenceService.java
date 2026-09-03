package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Inventory;

import java.util.List;
import java.util.Optional;

@Local
public interface InventoryPersistenceService {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(Long id);
    Optional<Inventory> findBySku(String sku);
    List<Inventory> findAll();
    List<Inventory> findBelowReorderThreshold();
    boolean deductStock(String sku, int quantity);
    void restock(String sku, int quantity);
    void delete(Long id);
}
