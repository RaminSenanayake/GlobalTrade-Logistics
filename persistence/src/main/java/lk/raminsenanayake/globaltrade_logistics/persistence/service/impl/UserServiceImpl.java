package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.User;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.UserService;

import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

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
}
