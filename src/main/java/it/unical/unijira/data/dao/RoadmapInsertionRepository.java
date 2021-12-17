package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.RoadmapInsertion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapInsertionRepository extends JpaRepository<RoadmapInsertion, Long> {
}
