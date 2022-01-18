package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.services.common.ProductBacklogInsertionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record ProductBacklogInsertionServiceImpl (ProductBacklogInsertionRepository backlogInsertionRepository)
        implements ProductBacklogInsertionService {
    @Override
    public Optional<ProductBacklogInsertion> save(ProductBacklogInsertion backlogIns) {
        if(backlogIns.getBacklog() == null ||
                backlogIns.getItem() == null) {

            return Optional.empty();
        }

        return Optional.of(backlogInsertionRepository.saveAndFlush(backlogIns));
    }

    @Override
    public Optional<ProductBacklogInsertion> update(Long id, ProductBacklogInsertion backlogIns) {
        try {
            return backlogInsertionRepository.findById(id)
                    .stream()
                    .peek(updatedItem -> {
                        if(backlogIns.getBacklog() != null) {
                            if (backlogIns.getBacklog().getProject() == null || !updatedItem.getBacklog().getProject().getId().equals(backlogIns.getBacklog().getProject().getId()))
                                throw new DataIntegrityViolationException("An item can be assigned to a unique project");

                            updatedItem.setBacklog(backlogIns.getBacklog());
                        }

                        updatedItem.setPriority(backlogIns.getPriority());
                    })
                    .findFirst()
                    .map(backlogInsertionRepository::saveAndFlush);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(ProductBacklogInsertion backlogIns) {
        backlogInsertionRepository.delete(backlogIns);

    }

    @Override
    public Optional<ProductBacklogInsertion> findById(Long id) {
        return backlogInsertionRepository.findById(id);
    }

    @Override
    public List<ProductBacklogInsertion> findAll() {
       return backlogInsertionRepository.findAll();
    }

    @Override
    public List<ProductBacklogInsertion> findAllByBacklog(ProductBacklog backlog, int page, int size) {
        return backlogInsertionRepository.findAllByBacklog(backlog, PageRequest.of(page, size));
    }
}
