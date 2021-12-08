package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;

import java.net.URL;
import java.util.List;
import java.util.Optional;


public interface NotifyService {

    void send(User user, String title, String message, URL target, Notify.Priority priority);

    Optional<Notify> create(Notify notify);
    Optional<Notify> update(Long id, Notify notify);
    Optional<Notify> findById(Long id);

    List<Notify> findAllByUserId(Long userId, int page, int size);
  
    Optional<List<Notify>> findAll();
}
