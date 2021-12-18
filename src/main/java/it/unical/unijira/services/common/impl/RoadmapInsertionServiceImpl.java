package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.RoadmapInsertionRepository;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import it.unical.unijira.services.common.RoadmapInsertionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record RoadmapInsertionServiceImpl(RoadmapInsertionRepository roadmapInsertionRepository)
        implements RoadmapInsertionService {
    @Override
    public Optional<RoadmapInsertion> save(RoadmapInsertion roadmapInsertion) {
        return Optional.of(roadmapInsertionRepository.save(roadmapInsertion));
    }

    @Override
    public Optional<RoadmapInsertion> update(Long id, RoadmapInsertion roadmapInsertion) {
        return roadmapInsertionRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setStartingDate(roadmapInsertion.getStartingDate());
                    updatedItem.setEndingDate((roadmapInsertion.getEndingDate()));
                    updatedItem.setRoadmap(roadmapInsertion.getRoadmap());
                    updatedItem.setPbi(roadmapInsertion.getPbi());
                })
                .findFirst()
                .map(roadmapInsertionRepository::saveAndFlush);
    }

    @Override
    public void delete(RoadmapInsertion roadmapInsertion) {
        roadmapInsertionRepository.delete(roadmapInsertion);
    }

    @Override
    public Optional<RoadmapInsertion> findById(Long id) {
        return roadmapInsertionRepository.findById(id);
    }

    @Override
    public List<RoadmapInsertion> findAll() {
        return roadmapInsertionRepository.findAll();
    }

    @Override
    public List<RoadmapInsertion> findAllByRoadmap(Roadmap roadmap) {
        return roadmapInsertionRepository.findAllByRoadmap(roadmap);
    }
}
