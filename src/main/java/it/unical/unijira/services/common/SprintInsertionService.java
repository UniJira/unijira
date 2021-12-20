package it.unical.unijira.services.common;

import it.unical.unijira.data.models.*;

import java.util.List;
import java.util.Optional;

public interface SprintInsertionService {

    Optional<SprintInsertion> save(SprintInsertion sprintInsertion);
    Optional<SprintInsertion> update (Long id, SprintInsertion sprintInsertion);
    void delete (SprintInsertion sprintInsertion);
    Optional<SprintInsertion> findById(Long id);
    List<SprintInsertion> findAll();
    List<SprintInsertion> findItemsBySprint(Sprint s, int page, int size);
}
