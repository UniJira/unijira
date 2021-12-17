package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ProductBacklogInsertion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBacklogInsertionRepository extends JpaRepository<ProductBacklogInsertion, Long> {
}
