package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.models.*;
import it.unical.unijira.services.common.SprintInsertionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record SprintInsertionServiceImpl(SprintInsertionRepository sprintInsertionRepository)
        implements SprintInsertionService {

    @Override
    public Optional<SprintInsertion> save(SprintInsertion sprintInsertion) {
        return Optional.of(sprintInsertionRepository.saveAndFlush(sprintInsertion));
    }

    @Override
    public Optional<SprintInsertion> update(Long id, SprintInsertion sprintInsertion) {

        return sprintInsertionRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setItem(sprintInsertion.getItem());
                    updatedItem.setSprint(sprintInsertion.getSprint());
                })
                .findFirst()
                .map(sprintInsertionRepository::saveAndFlush);
    }

    @Override
    public void delete(SprintInsertion sprintInsertion) {
        sprintInsertionRepository.delete(sprintInsertion);
    }

    @Override
    public Optional<SprintInsertion> findById(Long id) {
        return sprintInsertionRepository.findById(id);
    }

    @Override
    public List<SprintInsertion> findAll() {
        return sprintInsertionRepository.findAll();
    }


    @Override
    public List<SprintInsertion> findItemsBySprint(Sprint s, int page, int size) {
        return sprintInsertionRepository.findItemsBySprint(s, PageRequest.of(page, size));
    }
}
