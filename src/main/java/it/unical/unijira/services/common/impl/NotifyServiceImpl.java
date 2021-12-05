package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.NotifyRepository;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.services.common.NotifyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record NotifyServiceImpl(NotifyRepository notifyRepository) implements NotifyService {

    @Override
    public Optional<Notify> findById(Long id) {
        return notifyRepository.findById(id);
    }

    @Override
    public Optional<Notify> markAsRead(Long id) {

        return notifyRepository.findById(id)
                .stream()
                .peek(notify -> notify.setRead(true))
                .peek(notifyRepository::save)
                .findFirst();

    }

    @Override
    public List<Notify> findAllByUserId(Long userId, int page, int size) {
        return notifyRepository.findByUserIdOrderByReadAscPriorityAscDateDesc(userId, PageRequest.of(page, size));
    }

}
