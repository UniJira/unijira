package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapInsertionRepository extends JpaRepository<RoadmapInsertion, Long> {
    @Query(value = "FROM RoadmapInsertion ri where ri.roadmap = :roadmap")
    List<RoadmapInsertion> findAllByRoadmap(Roadmap roadmap, Pageable pageable);
}
