package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Vendor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.VendorPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class VendorPersistenceServiceImpl implements VendorPersistenceService {

    @PersistenceContext(unitName = "globalTrade-logistics")
    private EntityManager em;

    @Override
    public Vendor save(Vendor vendor) {
        if (vendor.getId() == null) {
            em.persist(vendor);
            return vendor;
        } else {
            return em.merge(vendor);
        }
    }

    @Override
    public Optional<Vendor> findById(Long id) {
        return Optional.ofNullable(em.find(Vendor.class, id));
    }

    @Override
    public Optional<Vendor> findByVendorCode(String vendorCode) {
        try {
            Vendor v = em.createQuery("SELECT v FROM Vendor v WHERE v.vendorCode = :code", Vendor.class)
                    .setParameter("code", vendorCode)
                    .getSingleResult();
            return Optional.of(v);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Vendor> findAll() {
        return em.createQuery("SELECT v FROM Vendor v ORDER BY v.name", Vendor.class)
                .getResultList();
    }

    @Override
    public List<Vendor> findByComplianceStatus(VendorComplianceStatus status) {
        return em.createQuery("SELECT v FROM Vendor v WHERE v.complianceStatus = :status", Vendor.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public void updatePerformance(String vendorCode, double rating, double onTimeRate, int totalHandled, int delayedCount) {
        findByVendorCode(vendorCode).ifPresent(v -> {
            v.setPerformanceRating(rating);
            v.setOnTimeDeliveryRate(onTimeRate);
            v.setTotalShipmentsHandled(totalHandled);
            v.setDelayedShipmentsCount(delayedCount);
            v.setLastEvaluatedAt(LocalDateTime.now());
            if (onTimeRate < 50.0 || rating < 1.5) {
                v.setComplianceStatus(VendorComplianceStatus.SUSPENDED);
            } else if (onTimeRate < 70.0 || rating < 2.5) {
                v.setComplianceStatus(VendorComplianceStatus.UNDER_REVIEW);
            } else {
                v.setComplianceStatus(VendorComplianceStatus.COMPLIANT);
            }
            em.merge(v);
        });
    }

    @Override
    public void update(Long id, Vendor vendor) {
        findById(id).ifPresent(v -> {
            v.setVendorCode(vendor.getVendorCode());
            v.setName(vendor.getName());
            v.setCountry(vendor.getCountry());
            v.setContactEmail(vendor.getContactEmail());
            em.merge(v);
        });
    }

    @Override
    public void delete(Long id) {
        Vendor v = em.find(Vendor.class, id);
        if (v != null) {
            em.remove(v);
        }
    }
}
