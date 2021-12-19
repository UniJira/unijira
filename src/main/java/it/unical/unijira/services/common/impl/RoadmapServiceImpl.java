package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.RoadmapRepository;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.services.common.RoadmapService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record RoadmapServiceImpl(RoadmapRepository roadmapRepository)
        implements RoadmapService {
    @Override
    public Optional<Roadmap> save(Roadmap roadmap) {
        return Optional.of(roadmapRepository.save(roadmap));
    }

    @Override
    public Optional<Roadmap> update(Long id, Roadmap roadmap) {
        return roadmapRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setBacklog(roadmap.getBacklog());
                })
                .findFirst()
                .map(roadmapRepository::saveAndFlush);
    }

    @Override
    public void delete(Roadmap roadmap) {
        roadmapRepository.delete(roadmap);
    }

    @Override
    public Optional<Roadmap> findById(Long id) {
        return roadmapRepository.findById(id);
    }

    @Override
    public List<Roadmap> findAll() {
        return roadmapRepository.findAll();
    }


    @Override
    public List<Roadmap> findByBacklog(ProductBacklog backlog) {
       return roadmapRepository.findByBacklog(backlog);
    }
}
