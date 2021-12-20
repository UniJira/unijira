package it.unical.unijira.services.common;

import it.unical.unijira.data.models.User;

import java.util.Optional;


public interface UserService {

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> save(User user);
    boolean activate(Long id);

}
