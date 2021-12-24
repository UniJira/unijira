package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserService {

    List<User> findAll();
    List<User> findAll(Integer page, Integer size);

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> save(User user);

    Optional<User> updateNotificationMask(Long id, Set<Notify.Mask> mask);

    boolean activate(Long id);

}
