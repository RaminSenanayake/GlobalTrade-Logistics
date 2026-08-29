package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import lk.raminsenanayake.globaltrade_logistics.persistence.entity.User;

import java.util.Optional;

public interface UserService {
    boolean validate(String username, String password);
    Optional<User> getUser(String username);
}
