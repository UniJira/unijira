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
        return Optional.of(sprintRepository.save(sprint));
    }

    @Override
    public Optional<Sprint> update(Long id, Sprint sprint) {
        return sprintRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setBacklog(sprint.getBacklog());
                    updatedItem.setEndingDate(sprint.getEndingDate());
                    updatedItem.setStartingDate(sprint.getStartingDate());
                    updatedItem.setInsertions(sprint.getInsertions());
                    })
                .findFirst()
                .map(sprintRepository::saveAndFlush);
    }

    @Override
    public void delete(Sprint sprint) {
        sprintRepository.delete(sprint);
    }

    @Override
    public Optional<Sprint> findById(Long id) {
        return sprintRepository.findById(id);
    }

    @Override
    public List<Sprint> findAll() {
        return sprintRepository.findAll();
    }

    @Override
    public List<Sprint> findSprintsByBacklog(ProductBacklog backlog) {
        return sprintRepository.sprintsOfABacklog(backlog);
    }
}
