package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.SprintInsertion;
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
        ProductBacklogInsertion productBacklogInsertion = sprintInsertion.getItem().getProductBacklogInsertion();

        if(productBacklogInsertion == null || !productBacklogInsertion.getBacklog().getId().equals(sprintInsertion.getSprint().getBacklog().getId()))
            return Optional.empty();

        return Optional.of(sprintInsertionRepository.saveAndFlush(sprintInsertion));
    }

    @Override
    public Optional<SprintInsertion> update(Long id, SprintInsertion sprintInsertion) {
        try {
            return sprintInsertionRepository.findById(id)
                    .stream()
                    .peek(updatedItem -> {
                        if (updatedItem.getItem().getId().equals(sprintInsertion.getItem().getId())) {
                            if (!updatedItem.getSprint().getBacklog().getId()
                                    .equals(sprintInsertion.getSprint().getBacklog().getId()))
                                throw new DataIntegrityViolationException("An item can be assigned to a unique project");
                        } else {
                            updatedItem.setItem(sprintInsertion.getItem());

                            ProductBacklogInsertion productBacklogInsertion = sprintInsertion.getItem().getProductBacklogInsertion();
                            if(productBacklogInsertion == null || !productBacklogInsertion.getBacklog().getId().equals(sprintInsertion.getSprint().getBacklog().getId()))
                                throw new DataIntegrityViolationException("An item can be assigned to a unique project");
                        }
                        updatedItem.setSprint(sprintInsertion.getSprint());
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
