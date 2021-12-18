package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;

import java.util.List;
import java.util.Optional;

public interface ProductBacklogInsertionService {

    Optional<ProductBacklogInsertion> save(ProductBacklogInsertion backlog);
    Optional<ProductBacklogInsertion> update (Long id, ProductBacklogInsertion backlog);
    void delete (ProductBacklogInsertion backlog);
    Optional<ProductBacklogInsertion> findById(Long id);
    List<ProductBacklogInsertion> findAll();
    List<ProductBacklogInsertion> findAllByBacklog(ProductBacklog backlog);
}
