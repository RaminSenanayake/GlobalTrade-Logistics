package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import lk.raminsenanayake.globaltrade_logistics.persistence.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenPersistenceService {
    Optional<RefreshToken> findValidToken(String refreshToken);
    RefreshToken createToken(String username);
    void deleteToken(String refreshToken);
    void deleteExpiredTokens();
}
