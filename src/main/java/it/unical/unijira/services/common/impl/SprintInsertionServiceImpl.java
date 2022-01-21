package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.SprintInsertion;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.services.common.SprintInsertionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record SprintInsertionServiceImpl(SprintInsertionRepository sprintInsertionRepository,
                                         ProductBacklogInsertionRepository productBacklogInsertionRepository)
        implements SprintInsertionService {

    @Override
    public Optional<SprintInsertion> save(SprintInsertion sprintInsertion) {
        if (sprintInsertion.getSprint() == null ||
                sprintInsertion.getSprint().getBacklog() == null ||
                sprintInsertion.getItem() == null) {

            return Optional.empty();
        }

        if(sprintInsertionRepository.findAllByItemId(sprintInsertion.getItem().getId())
                .stream()
                .map(s -> s.getSprint().getId())
                .anyMatch(id ->
                        id.equals(sprintInsertion.getSprint().getId()))
        ) {

            return Optional.empty();
        }

        Optional<ProductBacklogInsertion> productBacklogInsertion = productBacklogInsertionRepository.findByItemId(sprintInsertion.getItem().getId());

        if(productBacklogInsertion.isEmpty() || !productBacklogInsertion.get().getBacklog().getId().equals(sprintInsertion.getSprint().getBacklog().getId()))
            return Optional.empty();

        return Optional.of(sprintInsertionRepository.saveAndFlush(sprintInsertion));
    }

    @Override
    public Optional<SprintInsertion> update(Long id, SprintInsertion sprintInsertion) {
        try {
            return sprintInsertionRepository.findById(id)
                    .stream()
                    .peek(updatedItem -> {
                        Item oldItem = updatedItem.getItem();
                        Sprint oldSprint = updatedItem.getSprint();

                        if(sprintInsertion.getItem() != null)
                            updatedItem.setItem(sprintInsertion.getItem());

                        if(sprintInsertion.getSprint() != null) {
                            if(sprintInsertion.getSprint().getBacklog() == null)
                                throw new DataIntegrityViolationException("Format not allowed");

                            updatedItem.setSprint(sprintInsertion.getSprint());
                        }

                        if (oldItem.getId().equals(updatedItem.getItem().getId())) {
                            if (!oldSprint.getBacklog().getId().equals(updatedItem.getSprint().getBacklog().getId()))
                                throw new DataIntegrityViolationException("An item can be assigned to a unique project");
                        } else {
                            Optional<ProductBacklogInsertion> productBacklogInsertion = productBacklogInsertionRepository.findByItemId(updatedItem.getItem().getId());
                            if(productBacklogInsertion.isEmpty() || !productBacklogInsertion.get().getBacklog().getId().equals(updatedItem.getSprint().getBacklog().getId()))
                                throw new DataIntegrityViolationException("An item can be assigned to a unique project");
                        }
                    })
                    .findFirst()
                    .map(sprintInsertionRepository::saveAndFlush);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
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
