package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.SprintRepository;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.services.common.SprintService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record SprintServiceImpl(SprintRepository sprintRepository)
        implements SprintService {
    @Override
    public Optional<Sprint> save(Sprint sprint) {
        return Optional.empty();
    }

    @Override
    public Optional<Sprint> update(Long id, Sprint sprint) {
        return Optional.empty();
    }

    @Override
    public void delete(Sprint sprint) {

    }

    @Override
    public Optional<Sprint> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Sprint> findAll() {
        return null;
    }

    @Override
    public List<Sprint> findSprintsByBacklog(ProductBacklog backlog) {
        return null;
    }
}
