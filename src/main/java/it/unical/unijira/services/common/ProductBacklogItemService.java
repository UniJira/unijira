package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ProductBacklogItem;

import java.util.List;
import java.util.Optional;

public interface ProductBacklogItemService {
    Optional<ProductBacklogItem> save (ProductBacklogItem pbi);
    Optional<ProductBacklogItem> update (Long id, ProductBacklogItem pbi);
    void delete(ProductBacklogItem pbi);
    Optional<ProductBacklogItem> findById(Long id);
    List<ProductBacklogItem> findAll();
    List<ProductBacklogItem> findAllByFather(Long fatherId, int page, int size);
    List<ProductBacklogItem> findAllByUser(Long userId, int page, int size);

}
