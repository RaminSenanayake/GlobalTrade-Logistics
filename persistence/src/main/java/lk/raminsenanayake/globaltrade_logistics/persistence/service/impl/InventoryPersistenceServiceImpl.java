package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Inventory;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.InventoryPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class InventoryPersistenceServiceImpl implements InventoryPersistenceService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Inventory save(Inventory inventory) {
        if (inventory.getId() == 0) {
            em.persist(inventory);
            return inventory;
        } else {
            return em.merge(inventory);
        }
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return Optional.ofNullable(em.find(Inventory.class, id));
    }

    @Override
    public Optional<Inventory> findBySku(String sku) {
        try {
            Inventory item = em.createQuery("SELECT i FROM Inventory i WHERE i.sku = :sku", Inventory.class)
                    .setParameter("sku", sku)
                    .getSingleResult();
            return Optional.of(item);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Inventory> findAll() {
        return em.createQuery("SELECT i FROM Inventory i ORDER BY i.name", Inventory.class)
                .getResultList();
    }

    @Override
    public List<Inventory> findBelowReorderThreshold() {
        return em.createQuery("SELECT i FROM Inventory i WHERE i.qty <= i.reorderThreshold", Inventory.class)
                .getResultList();
    }

    @Override
    public boolean deductStock(String sku, int quantity) {
        Optional<Inventory> opt = findBySku(sku);
        if (opt.isPresent()) {
            Inventory item = opt.get();
            if (item.getQty() >= quantity) {
                item.setQty(item.getQty() - quantity);
                em.merge(item);
                return true;
            }
        }
        return false;
    }

    @Override
    public void restock(String sku, int quantity) {
        Optional<Inventory> opt = findBySku(sku);
        if (opt.isPresent()) {
            Inventory item = opt.get();
            item.setQty(item.getQty() + quantity);
            item.setLastRestocked(LocalDateTime.now());
            em.merge(item);
        }
    }

    @Override
    public void delete(Long id) {
        Inventory item = em.find(Inventory.class, id);
        if (item != null) {
            em.remove(item);
        }
    }
}
