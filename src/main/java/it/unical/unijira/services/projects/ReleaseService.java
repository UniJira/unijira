package it.unical.unijira.services.projects;

import it.unical.unijira.data.models.projects.releases.Release;

import java.util.List;
import java.util.Optional;

public interface ReleaseService {

    List<Release> findAllByProjectId(Long projectId);

    Optional<Release> findById(Long id);
    Optional<Release> create(Release release);
    Optional<Release> update(Long id, Release release);

}
