package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProjectRepository;
import it.unical.unijira.data.models.Project;
import it.unical.unijira.services.common.ProjectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public record ProjectServiceImpl(ProjectRepository projectRepository) implements ProjectService {

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Optional<Project> save(Project project) {
        return Optional.of(projectRepository.saveAndFlush(project));
    }

    @Override
    public Optional<Project> create(Project project) {

        var p = new Project();
        p.setName(project.getName());
        p.setKey(project.getKey());
        p.setOwner(project.getOwner());
        p.setMemberships(Collections.emptyList());

        return Optional.of(projectRepository.saveAndFlush(p));

    }

    @Override
    public Optional<Project> update(Long id, Project project) {

        return projectRepository.findById(id)
                .stream()
                .peek(p -> {
                    p.setName(project.getName());
                    p.setKey(project.getKey());
                    p.setOwner(project.getOwner());
                    p.setMemberships(project.getMemberships());
                })
                .findFirst()
                .map(projectRepository::saveAndFlush);
    }

    @Override
    public void delete(Project project) {
        projectRepository.delete(project);
    }

    @Override
    public List<Project> findAllByOwnerId(Long userId, int page, int size) {
        return projectRepository.findByOwnerId(userId, PageRequest.of(page, size));
    }

    @Override
    public List<Project> findAllByMemberId(Long userId, int page, int size) {
        return projectRepository.findByMembershipsUserId(userId, PageRequest.of(page, size));
    }

}
