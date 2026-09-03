package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclaration;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclarationStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.CustomsPersistenceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class CustomsPersistenceServiceImpl implements CustomsPersistenceService {

    @PersistenceContext(unitName = "globalTrade-logistics")
    private EntityManager em;

    @Override
    public CustomsDeclaration save(CustomsDeclaration declaration) {
        if (declaration.getId() == null) {
            em.persist(declaration);
            return declaration;
        } else {
            return em.merge(declaration);
        }
    }

    @Override
    public Optional<CustomsDeclaration> findById(Long id) {
        return Optional.ofNullable(em.find(CustomsDeclaration.class, id));
    }

    @Override
    public Optional<CustomsDeclaration> findByDeclarationNumber(String declarationNumber) {
        try {
            CustomsDeclaration cd = em.createQuery(
                    "SELECT cd FROM CustomsDeclaration cd WHERE cd.declarationNumber = :num", CustomsDeclaration.class)
                    .setParameter("num", declarationNumber)
                    .getSingleResult();
            return Optional.of(cd);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CustomsDeclaration> findByTrackingNumber(String trackingNumber) {
        try {
            CustomsDeclaration cd = em.createQuery(
                    "SELECT cd FROM CustomsDeclaration cd WHERE cd.trackingNumber = :tn", CustomsDeclaration.class)
                    .setParameter("tn", trackingNumber)
                    .getSingleResult();
            return Optional.of(cd);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CustomsDeclaration> findAll() {
        return em.createQuery("SELECT cd FROM CustomsDeclaration cd ORDER BY cd.createdAt DESC", CustomsDeclaration.class)
                .getResultList();
    }

    @Override
    public List<CustomsDeclaration> findByStatus(CustomsDeclarationStatus status) {
        return em.createQuery("SELECT cd FROM CustomsDeclaration cd WHERE cd.status = :status", CustomsDeclaration.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<CustomsDeclaration> findApproachingDeadlines(LocalDateTime threshold) {
        return em.createQuery(
                "SELECT cd FROM CustomsDeclaration cd WHERE cd.status IN (:s1, :s2) AND cd.filingDeadline <= :threshold", CustomsDeclaration.class)
                .setParameter("s1", CustomsDeclarationStatus.PENDING_SUBMISSION)
                .setParameter("s2", CustomsDeclarationStatus.SUBMITTED)
                .setParameter("threshold", threshold)
                .getResultList();
    }

    @Override
    public void updateStatus(String declarationNumber, CustomsDeclarationStatus status, String reviewedBy, String notes) {
        findByDeclarationNumber(declarationNumber).ifPresent(cd -> {
            cd.setStatus(status);
            cd.setReviewedBy(reviewedBy);
            cd.setComplianceNotes(notes);
            if (status == CustomsDeclarationStatus.APPROVED) {
                cd.setClearanceDate(LocalDateTime.now());
            }
            em.merge(cd);
        });
    }
}
