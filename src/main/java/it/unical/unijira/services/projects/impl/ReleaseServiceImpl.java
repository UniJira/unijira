package it.unical.unijira.services.projects.impl;

import it.unical.unijira.data.dao.projects.ProjectRepository;
import it.unical.unijira.data.dao.projects.ReleaseRepository;
import it.unical.unijira.data.models.projects.releases.Release;
import it.unical.unijira.services.projects.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReleaseServiceImpl implements ReleaseService {

    private final ReleaseRepository releaseRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ReleaseServiceImpl(ReleaseRepository releaseRepository, ProjectRepository projectRepository) {
        this.releaseRepository = releaseRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Release> findAllByProjectId(Long projectId) {
        return releaseRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId);
    }

    @Override
    public Optional<Release> findById(Long id) {
        return releaseRepository.findById(id);
    }

    @Override
    public Optional<Release> create(Release release) {

        if(projectRepository.findById(release.getProject().getId())
                .stream()
                .peek(release::setProject)
                .findFirst()
                .isPresent()) {

            return Optional.of(releaseRepository.saveAndFlush(release));

        }

        return Optional.empty();

    }

    @Override
    public Optional<Release> update(Long id, Release release) {
        return releaseRepository.findById(id).stream()
                .peek(r -> {

                    r.setVersion(release.getVersion());
                    r.setDescription(release.getDescription());
                    r.setStatus(release.getStatus());
                    r.setStartDate(release.getStartDate());
                    r.setEndDate(release.getEndDate());

                    // TODO: Send notifucation about release update

                }).map(releaseRepository::saveAndFlush)
                  .findFirst();
    }
}
