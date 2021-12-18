package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;

import java.util.List;
import java.util.Optional;

public interface RoadmapInsertionService {

    Optional<RoadmapInsertion> save(RoadmapInsertion roadmapInsertion);
    Optional<RoadmapInsertion> update (Long id, RoadmapInsertion roadmapInsertion);
    void delete (RoadmapInsertion roadmapInsertion);
    Optional<RoadmapInsertion> findById(Long id);
    List<RoadmapInsertion> findAll();
    List<RoadmapInsertion> findAllByRoadmap(Roadmap roadmap);
}
