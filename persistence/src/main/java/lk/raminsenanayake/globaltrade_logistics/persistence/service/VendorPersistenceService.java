package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Vendor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;

import java.util.List;
import java.util.Optional;

@Local
public interface VendorPersistenceService {
    Vendor save(Vendor vendor);
    Optional<Vendor> findById(Long id);
    Optional<Vendor> findByVendorCode(String vendorCode);
    List<Vendor> findAll();
    List<Vendor> findByComplianceStatus(VendorComplianceStatus status);
    void updatePerformance(String vendorCode, double rating, double onTimeRate, int totalHandled, int delayedCount);
    void update(Long id, Vendor vendor);
    void delete(Long id);
}
