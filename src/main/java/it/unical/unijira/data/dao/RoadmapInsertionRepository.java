package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import it.unical.unijira.data.models.items.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapInsertionRepository extends JpaRepository<RoadmapInsertion, Long> {
    @Query(value = "FROM RoadmapInsertion ri where ri.roadmap = :roadmap")
    List<RoadmapInsertion> findAllByRoadmap(Roadmap roadmap, Pageable pageable);

    @Query(value = "FROM RoadmapInsertion ri where ri.item = :item and ri.roadmap = :roadmap")
    List<RoadmapInsertion> findByItemAndRoadmap(Item item, Roadmap roadmap);

    List<RoadmapInsertion> findAllByItemId(Long id);
}
