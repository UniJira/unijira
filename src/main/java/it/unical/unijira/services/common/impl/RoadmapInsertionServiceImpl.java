package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.dao.RoadmapInsertionRepository;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.services.common.RoadmapInsertionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public record RoadmapInsertionServiceImpl(RoadmapInsertionRepository roadmapInsertionRepository,
                                          ProductBacklogInsertionRepository productBacklogInsertionRepository)
        implements RoadmapInsertionService {
    @Override
    public Optional<RoadmapInsertion> save(RoadmapInsertion roadmapInsertion) {
        if (roadmapInsertion.getRoadmap() == null ||
                roadmapInsertion.getRoadmap().getBacklog() == null ||
                roadmapInsertion.getItem() == null) {

            return Optional.empty();
        }

        Optional<ProductBacklogInsertion> productBacklogInsertion = productBacklogInsertionRepository.findByItemId(roadmapInsertion.getItem().getId());

        if(productBacklogInsertion.isEmpty() || !productBacklogInsertion.get().getBacklog().getId().equals(roadmapInsertion.getRoadmap().getBacklog().getId()))
            return Optional.empty();

        return Optional.of(roadmapInsertionRepository.saveAndFlush(roadmapInsertion));
    }

    @Override
    public Optional<RoadmapInsertion> update(Long id, RoadmapInsertion roadmapInsertion) {
        try {
            return roadmapInsertionRepository.findById(id)
                    .stream()
                    .peek(updatedItem -> {
                        updatedItem.setStartingDate(roadmapInsertion.getStartingDate());
                        updatedItem.setEndingDate((roadmapInsertion.getEndingDate()));

                        Item oldItem = updatedItem.getItem();
                        Roadmap oldRoadmap = updatedItem.getRoadmap();

                        if(roadmapInsertion.getItem() != null)
                            updatedItem.setItem(roadmapInsertion.getItem());

                        if(roadmapInsertion.getRoadmap() != null) {
                            if(roadmapInsertion.getRoadmap().getBacklog() == null)
                                throw new DataIntegrityViolationException("Format not allowed");

                            updatedItem.setRoadmap(roadmapInsertion.getRoadmap());
                        }

                        if (oldItem.getId().equals(updatedItem.getItem().getId())) {
                            if (!oldRoadmap.getBacklog().getId().equals(updatedItem.getRoadmap().getBacklog().getId()))
                                throw new DataIntegrityViolationException("An item can be assigned to a unique project");
                        } else {
                            Optional<ProductBacklogInsertion> productBacklogInsertion = productBacklogInsertionRepository.findByItemId(updatedItem.getItem().getId());
                            if(productBacklogInsertion.isEmpty() || !productBacklogInsertion.get().getBacklog().getId().equals(updatedItem.getRoadmap().getBacklog().getId()))
                                throw new DataIntegrityViolationException("An item can be assigned to a unique project");
                        }
                    })
                    .findFirst()
                    .map(roadmapInsertionRepository::saveAndFlush);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
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
    public List<RoadmapInsertion> findAllByRoadmap(Roadmap roadmap, int page, int size) {
        return roadmapInsertionRepository.findAllByRoadmap(roadmap, PageRequest.of(page, size));
    }

    @Override
    public List<RoadmapInsertion> findByItemAndRoadmap(Item item, Roadmap roadmap) {
        if (item != null && roadmap != null ) {
            return roadmapInsertionRepository.findByItemAndRoadmap(item, roadmap);
        }
        return Collections.emptyList();
    }
}
