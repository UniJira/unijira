package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.SprintInsertion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintInsertionRepository extends JpaRepository<SprintInsertion, Long> {
}
