package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlert;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertSeverity;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertType;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;

import java.util.List;

@ApplicationScoped
public class AlertPersistenceServiceImpl implements AlertPersistenceService {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public SupplyChainAlert recordAlert(SupplyChainAlertType type, SupplyChainAlertSeverity severity, String title, String message, String refCode) {
        SupplyChainAlert alert = new SupplyChainAlert(type, severity, title, message, refCode);
        em.persist(alert);
        return alert;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<SupplyChainAlert> findUnacknowledged() {
        return em.createQuery("SELECT a FROM SupplyChainAlert a WHERE a.acknowledged = false ORDER BY a.createdAt DESC", SupplyChainAlert.class)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<SupplyChainAlert> findAll(int maxResults) {
        return em.createQuery("SELECT a FROM SupplyChainAlert a ORDER BY a.createdAt DESC", SupplyChainAlert.class)
                .setMaxResults(maxResults > 0 ? maxResults : 100)
                .getResultList();
    }

    @Override
    @Transactional
    public void acknowledgeAlert(Long id) {
        SupplyChainAlert alert = em.find(SupplyChainAlert.class, id);
        if (alert != null) {
            alert.setAcknowledged(true);
            em.merge(alert);
        }
    }
}
