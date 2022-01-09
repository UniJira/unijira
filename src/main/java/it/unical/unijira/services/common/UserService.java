package it.unical.unijira.services.common;

import it.unical.unijira.data.models.User;

import java.util.List;
import java.util.Optional;


public interface UserService {

    List<User> findAll();
    List<User> findAll(Integer page, Integer size);

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> save(User user);

    Optional<User> resetPassword(Long id, String password);

    boolean activate(Long id);

}
