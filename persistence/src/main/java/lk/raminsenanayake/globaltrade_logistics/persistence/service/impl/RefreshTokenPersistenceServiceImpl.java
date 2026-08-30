package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.RefreshToken;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.RefreshTokenPersistenceService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
@Transactional
public class RefreshTokenPersistenceServiceImpl implements RefreshTokenPersistenceService {
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    @PersistenceContext
    private EntityManager em;


    @Override
    public Optional<RefreshToken> findValidToken(String refreshToken) {
        return em.createQuery("SELECT rf FROM RefreshToken rf WHERE rf.token=:token AND rf.expiryAt > :now", RefreshToken.class)
                .setParameter("token", refreshToken)
                .setParameter("now", LocalDateTime.now())
                .getResultStream().findFirst();
    }

    @Override
    public RefreshToken createToken(String username) {
        String token = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");

        LocalDateTime expiry = LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS);

        RefreshToken rt = new RefreshToken(username, token, expiry);

        em.persist(rt);
        return rt;
    }

    @Override
    public void deleteToken(String refreshToken) {
        em.createQuery("DELETE FROM RefreshToken rf WHERE rf.token=:token")
                .setParameter("token", refreshToken)
                .executeUpdate();
    }

    @Override
    public void deleteExpiredTokens() {
        em.createQuery("DELETE FROM RefreshToken rf WHERE rf.expiryAt > :now")
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
    }
}
