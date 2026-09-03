package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlert;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertSeverity;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertType;

import java.util.List;

@Local
public interface AlertPersistenceService {
    SupplyChainAlert recordAlert(SupplyChainAlertType type, SupplyChainAlertSeverity severity, String title, String message, String refCode);
    List<SupplyChainAlert> findUnacknowledged();
    List<SupplyChainAlert> findAll(int maxResults);
    void acknowledgeAlert(Long id);
}
