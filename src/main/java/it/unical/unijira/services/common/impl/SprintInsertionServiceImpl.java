package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.models.*;
import it.unical.unijira.services.common.SprintInsertionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record SprintInsertionServiceImpl(SprintInsertionRepository sprintInsertionRepository)
        implements SprintInsertionService {

    @Override
    public Optional<SprintInsertion> save(SprintInsertion sprintInsertion) {
        return Optional.empty();
    }

    @Override
    public Optional<SprintInsertion> update(Long id, SprintInsertion sprintInsertion) {
        return Optional.empty();
    }

    @Override
    public void delete(SprintInsertion sprintInsertion) {

    }

    @Override
    public Optional<SprintInsertion> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<SprintInsertion> findAll() {
        return sprintInsertionRepository.findAll();
    }


    @Override
    public List<ProductBacklogItem> findItemsBySprint(Sprint s) {
        return sprintInsertionRepository.findItemsBySprint(s);
    }
}
