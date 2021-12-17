package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ProductBacklog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBacklogRepository extends JpaRepository<ProductBacklog,Long> {
}
