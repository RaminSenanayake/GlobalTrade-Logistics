package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.User;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.UserRole;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.UserPersistenceService;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class UserPersistenceServiceImpl implements UserPersistenceService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Pbkdf2PasswordHash pbkdf2PasswordHash;

    @Override
    public boolean validate(String username, String password) {
        return getUser(username)
                .map(user -> pbkdf2PasswordHash.verify(password.toCharArray(), user.getPassword()))
                .orElse(false);
    }

    @Override
    public Optional<User> getUser(String username) {
        try {
            return Optional.of(em.createQuery("SELECT u FROM User u WHERE u.username=:username", User.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public User createUser(String username, String rawPassword, UserRole role) {
        String hashedPassword = pbkdf2PasswordHash.generate(rawPassword.toCharArray());
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole(role);
        em.persist(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.id", User.class)
                .getResultList();
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public void updateUserRole(String username, UserRole role) {
        getUser(username).ifPresent(user -> {
            user.setRole(role);
            em.merge(user);
        });
    }
}
