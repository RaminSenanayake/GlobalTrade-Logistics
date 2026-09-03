package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlert;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertSeverity;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertType;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;

import java.util.List;

@Stateless
public class AlertPersistenceServiceImpl implements AlertPersistenceService {

    @PersistenceContext(unitName = "globalTrade-logistics")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SupplyChainAlert recordAlert(SupplyChainAlertType type, SupplyChainAlertSeverity severity, String title, String message, String refCode) {
        SupplyChainAlert alert = new SupplyChainAlert(type, severity, title, message, refCode);
        em.persist(alert);
        return alert;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<SupplyChainAlert> findUnacknowledged() {
        return em.createQuery("SELECT a FROM SupplyChainAlert a WHERE a.acknowledged = false ORDER BY a.createdAt DESC", SupplyChainAlert.class)
                .getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<SupplyChainAlert> findAll(int maxResults) {
        return em.createQuery("SELECT a FROM SupplyChainAlert a ORDER BY a.createdAt DESC", SupplyChainAlert.class)
                .setMaxResults(maxResults > 0 ? maxResults : 100)
                .getResultList();
    }

    @Override
    public void acknowledgeAlert(Long id) {
        SupplyChainAlert alert = em.find(SupplyChainAlert.class, id);
        if (alert != null) {
            alert.setAcknowledged(true);
            em.merge(alert);
        }
    }
}
