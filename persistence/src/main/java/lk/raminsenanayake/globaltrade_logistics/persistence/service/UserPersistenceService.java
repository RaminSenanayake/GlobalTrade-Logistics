package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import lk.raminsenanayake.globaltrade_logistics.persistence.entity.User;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserPersistenceService {
    boolean validate(String username, String password);
    Optional<User> getUser(String username);
    User createUser(String username, String rawPassword, UserRole role);
    List<User> getAllUsers();
    boolean existsByUsername(String username);
    void updateUser(String username, User user);
}
