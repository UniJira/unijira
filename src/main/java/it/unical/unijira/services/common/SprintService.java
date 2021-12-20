package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Sprint;

import java.util.List;
import java.util.Optional;

public interface SprintService {

    Optional<Sprint> save(Sprint sprint);
    Optional<Sprint> update (Long id, Sprint sprint);
    void delete (Sprint sprint);
    Optional<Sprint> findById(Long id);
    List<Sprint> findAll();
    List<Sprint> findSprintsByBacklog(ProductBacklog backlog, int page, int size);
}
