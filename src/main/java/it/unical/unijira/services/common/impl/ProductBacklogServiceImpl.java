package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogItemRepository;
import it.unical.unijira.data.dao.ProductBacklogRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.services.common.ProductBacklogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record ProductBacklogServiceImpl(ProductBacklogRepository productBacklogRepository)
        implements ProductBacklogService {


    @Override
    public Optional<ProductBacklog> save(ProductBacklog backlog) {
        return Optional.of(productBacklogRepository.saveAndFlush(backlog));
    }

    @Override
    public Optional<ProductBacklog> update(Long id, ProductBacklog backlog) {

        return productBacklogRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setSprints(backlog.getSprints());
                    updatedItem.setProject(backlog.getProject());
                })
                .findFirst()
                .map(productBacklogRepository::saveAndFlush);
    }

    @Override
    public void delete(ProductBacklog backlog) {
        productBacklogRepository.delete(backlog);
    }

    @Override
    public Optional<ProductBacklog> findById(Long id) {
        return productBacklogRepository.findById(id);
    }

    @Override
    public List<ProductBacklog> findAll() {
        return productBacklogRepository.findAll();
    }

    @Override
    public List<ProductBacklogItem> findItems(ProductBacklog backlog) {
        return productBacklogRepository.findItems(backlog);
    }
}
