package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclaration;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclarationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Local
public interface CustomsPersistenceService {
    CustomsDeclaration save(CustomsDeclaration declaration);
    Optional<CustomsDeclaration> findById(Long id);
    Optional<CustomsDeclaration> findByDeclarationNumber(String declarationNumber);
    Optional<CustomsDeclaration> findByTrackingNumber(String trackingNumber);
    List<CustomsDeclaration> findAll();
    List<CustomsDeclaration> findByStatus(CustomsDeclarationStatus status);
    List<CustomsDeclaration> findApproachingDeadlines(LocalDateTime threshold);
    void updateStatus(String declarationNumber, CustomsDeclarationStatus status, String reviewedBy, String notes);
}
