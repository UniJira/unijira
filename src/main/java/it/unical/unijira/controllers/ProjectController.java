package it.unical.unijira.controllers;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.MembershipDTO;
import it.unical.unijira.data.dto.ProjectDTO;
import it.unical.unijira.data.models.Project;
import it.unical.unijira.services.common.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController implements CrudController<ProjectDTO, Long>  {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {

        return ResponseEntity.ok(projectService
                .findAllByMemberId(getAuthenticatedUser().getId(), page, size)
                .stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList()));

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDTO> read(ModelMapper modelMapper, Long id) {

        return projectService.findById(id)
                .stream()
                .filter(project -> project.getOwner().getId().equals(getAuthenticatedUser().getId()))
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDTO> create(ModelMapper modelMapper, ProjectDTO project) {

        if(project.getName().isBlank())
            return ResponseEntity.badRequest().build();

        if(project.getKey().isBlank())
            return ResponseEntity.badRequest().build();

        return projectService.create(modelMapper.map(project, Project.class))
                .map(p -> ResponseEntity
                        .created(URI.create("/projects/%d".formatted(p.getId())))
                        .body(modelMapper.map(p, ProjectDTO.class)))
                .orElse(ResponseEntity.badRequest().build());

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDTO> update(ModelMapper modelMapper, Long id, ProjectDTO project) {

        return projectService.update(id, modelMapper.map(project, Project.class))
                .map(p -> modelMapper.map(p, ProjectDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> delete(Long id) {

        return projectService.findById(id)
                .stream()
                .filter(project -> project.getOwner().getId().equals(getAuthenticatedUser().getId()))
                .peek(projectService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(project -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("{id}/memberships")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MembershipDTO>> readRoles(ModelMapper modelMapper, @PathVariable Long id, Integer page, Integer size) {

        return ResponseEntity.ok(projectService
                .findById(id)
                .stream()
                .map(p -> modelMapper.map(p.getMemberships(), MembershipDTO.class))
                .collect(Collectors.toList()));

    }

}
