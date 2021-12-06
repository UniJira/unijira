package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.NotifyRepository;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.EmailService;
import it.unical.unijira.services.common.NotifyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public record NotifyServiceImpl(NotifyRepository notifyRepository, EmailService emailService) implements NotifyService {

    @Override
    public void send(User user, String title, String message, URL target, Notify.Priority priority) {

        var n = new Notify();
        n.setTitle(title);
        n.setMessage(message);
        n.setPriority(priority);
        n.setUser(user);
        n.setTarget(target);

        this.create(n);

    }

    @Override
    public Optional<Notify> create(Notify notify) {

        var n = new Notify();
        n.setTitle(notify.getTitle());
        n.setMessage(notify.getMessage());
        n.setPriority(notify.getPriority());
        n.setUser(notify.getUser());
        n.setTarget(notify.getTarget());


        emailService.send (
                notify.getUser().getUsername(),
                notify.getTitle(),
                notify.getMessage()
        );

        return Optional.of(notifyRepository.saveAndFlush(n));

    }

    @Override
    public Optional<Notify> update(Long id, Notify notify) {

        return notifyRepository.findById(id)
                .stream()
                .peek(n -> n.setRead(notify.isRead()))
                .findFirst()
                .map(notifyRepository::saveAndFlush);

    }

    @Override
    public Optional<Notify> findById(Long id) {
        return notifyRepository.findById(id);
    }

    @Override
    public List<Notify> findAllByUserId(Long userId, int page, int size) {
        return notifyRepository.findByUserIdOrderByReadAscPriorityAscDateDesc(userId, PageRequest.of(page, size));
    }

}
