package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import it.unical.unijira.data.models.items.Item;

import java.util.List;
import java.util.Optional;

public interface RoadmapInsertionService {

    Optional<RoadmapInsertion> save(RoadmapInsertion roadmapInsertion);
    Optional<RoadmapInsertion> update (Long id, RoadmapInsertion roadmapInsertion);
    void delete (RoadmapInsertion roadmapInsertion);
    Optional<RoadmapInsertion> findById(Long id);
    List<RoadmapInsertion> findAll();
    List<RoadmapInsertion> findAllByRoadmap(Roadmap roadmap, int page, int size);
    List<RoadmapInsertion> findByItemAndRoadmap(Item item, Roadmap roadmap);
}
