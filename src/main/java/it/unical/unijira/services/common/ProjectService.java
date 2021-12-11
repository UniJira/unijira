package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    Optional<Project> findById(Long id);
    Optional<Project> save(Project project);
    Optional<Project> create(Project project);
    Optional<Project> update(Long id, Project project);
    void delete(Project project);
    List<Project> findAllByOwnerId(Long userId, int page, int size);
    List<Project> findAllByMemberId(Long userId, int page, int size);

}
