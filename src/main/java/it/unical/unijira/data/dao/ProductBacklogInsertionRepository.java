package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBacklogInsertionRepository extends JpaRepository<ProductBacklogInsertion, Long> {
    @Query(value = "FROM ProductBacklogInsertion pbi where pbi.backlog = :backlog")
    List<ProductBacklogInsertion> findAllByBacklog(ProductBacklog backlog, Pageable pageable);

    Optional<ProductBacklogInsertion> findByItemId(Long id);
}
