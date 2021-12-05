package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Notify;

import java.util.List;
import java.util.Optional;


public interface NotifyService {
    Optional<Notify> findById(Long id);
    Optional<Notify> markAsRead(Long id);
    List<Notify> findAllByUserId(Long userId, int page, int size);
}
