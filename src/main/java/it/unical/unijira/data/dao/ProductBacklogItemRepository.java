package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ProductBacklogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBacklogItemRepository extends JpaRepository<ProductBacklogItem, Long>,
        JpaSpecificationExecutor<ProductBacklogItem> {


    List<ProductBacklogItem> findAllByFather(ProductBacklogItem father);
}
