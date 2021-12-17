package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBacklogRepository extends JpaRepository<ProductBacklog,Long> {

    @Query(value = "SELECT insertion.item FROM ProductBacklogInsertion insertion where insertion.backlog = :backlog")
    List<ProductBacklogItem> findItems(ProductBacklog backlog);
}
