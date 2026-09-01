package lk.raminsenanayake.globaltrade_logistics.ejb_security.schedule;


import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.RefreshTokenPersistenceService;

@Singleton
@Startup
public class RefreshTokenCleanupScheduler {

    @Inject
    private RefreshTokenPersistenceService refreshTokenPersistenceService;

    @Schedule(persistent = false)
    @Transactional
    public void deleteExpiredRefreshTokens() {
        refreshTokenPersistenceService.deleteExpiredTokens();
    }
}
