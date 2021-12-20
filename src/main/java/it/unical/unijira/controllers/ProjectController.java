package it.unical.unijira.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.InviteMembersDTO;
import it.unical.unijira.data.dto.MembershipDTO;
import it.unical.unijira.data.dto.ProjectDTO;
import it.unical.unijira.data.models.MembershipKey;
import it.unical.unijira.data.models.Project;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.common.ProjectService;
import it.unical.unijira.services.common.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController implements CrudController<ProjectDTO, Long>  {

    private final UserService userService;
    private final ProjectService projectService;
    private final AuthService authService;

    @Autowired
    public ProjectController(UserService userService, ProjectService projectService, AuthService authService) {
        this.userService = userService;
        this.projectService = projectService;
        this.authService = authService;
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

        if(project.getOwnerId() != null)
            return ResponseEntity.badRequest().build();

        project.setOwnerId(getAuthenticatedUser().getId());

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

    @PostMapping("invitations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MembershipDTO>> inviteMembers(ModelMapper modelMapper, @RequestBody InviteMembersDTO inviteMembersDTO) {

        final var project = projectService.findById(inviteMembersDTO.getProjectId())
                .stream()
                .findAny()
                .orElse(null);

        if(project == null ) {
            return ResponseEntity.notFound().build();
        }

        if(getAuthenticatedUser().equals(project.getOwner())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var users = inviteMembersDTO.getEmails()
                .stream()
                .map(userService::findByUsername)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if(users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok( projectService.sendInvitations(project, users)
                .stream()
                .map(membership -> modelMapper.map(membership, MembershipDTO.class))
                .collect(Collectors.toList()));

    }

    @GetMapping("accept")
    public ResponseEntity<Boolean> accept(@RequestParam String token) {

        if(token.isBlank())
            return ResponseEntity.badRequest().build();

        try {

            var decoded = authService.verifyToken(token, TokenType.PROJECT_INVITE, "userId", "projectId");

            return Optional.of(new MembershipKey(
                    userService.findById(decoded.getClaim("userId").asLong())
                            .stream()
                            .findAny()
                            .orElse(null),
                    projectService.findById(decoded.getClaim("projectId").asLong())
                            .stream()
                            .findAny()
                            .orElse(null)))
                    .map(projectService::activate)
                    .map(v -> ResponseEntity.ok(true))
                    .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

}
