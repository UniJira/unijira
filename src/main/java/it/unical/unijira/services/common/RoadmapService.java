package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Roadmap;

import java.util.List;
import java.util.Optional;

public interface RoadmapService {

    Optional<Roadmap> save(Roadmap roadmap);
    Optional<Roadmap> update (Long id, Roadmap roadmap);
    void delete (Roadmap roadmap);
    Optional<Roadmap> findById(Long id);
    List<Roadmap> findAll();

    List<Roadmap> findByBacklog(ProductBacklog backlog, int page, int size);
}
