package it.unical.unijira.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.*;
import it.unical.unijira.data.dto.items.ItemDTO;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.MembershipKey;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.common.*;
import it.unical.unijira.utils.ControllerUtilities;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController implements CrudController<ProjectDTO, Long>  {

    private final UserService userService;
    private final ProjectService projectService;
    private final ProductBacklogService backlogService;
    private final ProductBacklogInsertionService insertionService;
    private final SprintService sprintService;
    private final SprintInsertionService sprintInsertionService;
    private final RoadmapService roadmapService;
    private final RoadmapInsertionService roadmapInsertionService;
    private final ItemService itemService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProjectController(UserService userService,
                             ProjectService projectService,
                             AuthService authService,
                             ProductBacklogService backlogService,
                             ProductBacklogInsertionService insertionService,
                             SprintService sprintService,
                             SprintInsertionService sprintInsertionService,
                             RoadmapService roadmapService,
                             RoadmapInsertionService roadmapInsertionService,
                             ItemService itemService,
                             PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.projectService = projectService;
        this.backlogService = backlogService;
        this.insertionService = insertionService;
        this.sprintService = sprintService;
        this.sprintInsertionService = sprintInsertionService;
        this.roadmapService = roadmapService;
        this.roadmapInsertionService = roadmapInsertionService;
        this.authService = authService;
        this.itemService = itemService;
        this.passwordEncoder = passwordEncoder;
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
    public ResponseEntity<List<MembershipDTO>> readMembership(ModelMapper modelMapper, @PathVariable Long id, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        return ResponseEntity.ok(projectService
                .findById(id)
                .stream()
                .flatMap(p -> p.getMemberships().stream())
                .map(p -> modelMapper.map(p, MembershipDTO.class))
                .collect(Collectors.toList()));

    }

    @PutMapping("{projectId}/memberships/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MembershipDTO> updateMembership(ModelMapper modelMapper, @PathVariable Long projectId, @PathVariable Long userId, @RequestBody MembershipDTO membership) {

        var m = modelMapper.map(membership, Membership.class);

        return projectService.updateMembership(projectId, userId, modelMapper.map(membership, Membership.class))
                .map(p -> modelMapper.map(p, MembershipDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }


    @PostMapping("invitations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MembershipDTO>> inviteMembers(ModelMapper modelMapper, @RequestBody InviteMembersDTO inviteMembersDTO) {

        final var project = projectService.findById(inviteMembersDTO.getProjectId()).orElse(null);

        if(project == null ) {
            return ResponseEntity.notFound().build();
        }

        if(getAuthenticatedUser().equals(project.getOwner())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        inviteMembersDTO.getEmails().forEach(mail -> {

            var user = User.builder()
                    .username(mail)
                    .password(passwordEncoder.encode(mail))
                    .activated(true)
                    .ownedProjects(Collections.emptyList())
                    .memberships(Collections.emptyList())
                    .build();

            userService.save(user);

        });

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

    @GetMapping("/{project}/backlogs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogDTO>> readAllBacklogs(ModelMapper modelMapper, @PathVariable Long project, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        Project projectObj = projectService.findById(project).orElse(null);

        if (!ControllerUtilities.checkProjectValidity(projectObj)) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(backlogService.findAllByProject(projectObj, page, size).stream()
                .map(item -> modelMapper.map(item, ProductBacklogDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{project}/backlogs/{backlog}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> readBacklog(ModelMapper modelMapper, @PathVariable Long project, @PathVariable Long backlog) {

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        return backlogOpt
                .stream()
                .map(x -> modelMapper.map(x, ProductBacklogDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{project}/backlogs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> createBacklog(ModelMapper modelMapper,  @PathVariable Long project, @RequestBody ProductBacklogDTO dto) {

        Project projectObj = projectService.findById(project).orElse(null);

        if (!ControllerUtilities.checkProjectValidity(projectObj)) {
            return ResponseEntity.notFound().build();
        }

        dto.setProjectId(project);

        return backlogService.save(modelMapper.map(dto, ProductBacklog.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/backlog/%d".formatted(createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ProductBacklogDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }


    // Per come è strutturata l'entità, l'update del backlog è una cosa assolutamente priva di senso, perché
    // sono praticamente tutte chiavi esterne, quindi basta chiamare l'update degli altri oggetti
   /* @PutMapping("/{project}/backlogs/{backlog}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> updateBacklog(ModelMapper modelMapper,  @PathVariable Long project,
                                                           @PathVariable Long backlog,
                                                           @RequestBody ProductBacklogDTO dto) {

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

       if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
           return ResponseEntity.notFound().build();
       }

        return backlogService.update(backlog, modelMapper.map(dto, ProductBacklog.class))
                .map(newDto -> modelMapper.map(newDto, ProductBacklogDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    */
    @DeleteMapping("/{project}/backlogs/{backlog}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteBacklog(@PathVariable Long project, @PathVariable Long backlog) {

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        return backlogService.findById(backlog)
                .stream()
                .peek(backlogService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(x -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{project}/backlogs/{backlog}/insertions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> addinsertionToBacklog(ModelMapper modelMapper,
                                                                       @PathVariable Long project,
                                                                       @PathVariable Long backlog,
                                                                       @RequestBody ProductBacklogInsertionDTO dto){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        dto.setBacklog(modelMapper.map(backlogObj, ProductBacklogDTO.class));
        ProductBacklogInsertion toSave = modelMapper.map(dto, ProductBacklogInsertion.class);
        return insertionService.save(toSave)
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("projects/%d/backlogs/%d/insertions/%d"
                                .formatted(project,backlog, createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ProductBacklogInsertionDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }


    @GetMapping("/{project}/backlogs/{backlog}/insertions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogInsertionDTO>> allFromBacklog(ModelMapper modelMapper,
                                                                           @PathVariable Long project,
                                                                           @PathVariable Long backlog,
                                                                           @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(insertionService.findAllByBacklog(backlogObj, page, size).stream()
                .map(insertion -> modelMapper.map(insertion, ProductBacklogInsertionDTO.class))
                .collect(Collectors.toList()));

    }

    @GetMapping("/{project}/backlogs/{backlog}/insertions/{insertion}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> insertionById(ModelMapper modelMapper,
                                                                    @PathVariable Long project,
                                                                    @PathVariable Long backlog,
                                                                    @PathVariable Long insertion){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<ProductBacklogInsertion> optional = insertionService.findById(insertion);
        ProductBacklogInsertion itemObj = optional.orElse(null);


        if (itemObj== null || itemObj.getItem() == null ||
                !ControllerUtilities.checkItemCoherence(projectObj,backlogObj,itemObj.getItem(),itemObj)) {
            return ResponseEntity.notFound().build();
        }
        return optional
                .stream()
                .map(found -> modelMapper.map(found, ProductBacklogInsertionDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("/{project}/backlogs/{backlog}/insertions/{insertion}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> updateInsertion(ModelMapper modelMapper, @PathVariable Long project,
                                                                      @PathVariable Long backlog, @PathVariable Long insertion,
                                                                      @RequestBody ProductBacklogInsertionDTO insertionDTO){

        Project projectObj = projectService.findById(project).orElse(null);
        ProductBacklogInsertion pbi = insertionService.findById(insertion).orElse(null);
        ProductBacklog backlogObj = backlogService.findById(backlog).orElse(null);

        if (pbi == null || pbi.getItem() == null ||
                !ControllerUtilities.checkItemCoherence(projectObj,backlogObj,pbi.getItem(),pbi)) {
            return ResponseEntity.notFound().build();
        }

        return insertionService.update(insertion, modelMapper.map(insertionDTO, ProductBacklogInsertion.class))
                .map(newDto -> modelMapper.map(newDto, ProductBacklogInsertionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{project}/backlogs/{backlog}/insertions/{insertion}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteInsertion(@PathVariable Long project, @PathVariable Long backlog,
                                                   @PathVariable Long insertion) {


        Optional<ProductBacklogInsertion> optional = insertionService.findById(insertion);
        ProductBacklogInsertion pbi = optional.orElse(null);

        Project projectObj = projectService.findById(project).orElse(null);
        ProductBacklog backlogObj = backlogService.findById(backlog).orElse(null);


        if (pbi == null || pbi.getItem() == null ||
                !ControllerUtilities.checkItemCoherence(projectObj,backlogObj,pbi.getItem(),pbi)) {
            return ResponseEntity.notFound().build();
        }

        return optional
                .stream()
                .peek(insertionService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(x -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{project}/backlogs/{backlog}/sprints")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintDTO> addSprint(ModelMapper modelMapper,
                                               @PathVariable Long project,
                                               @PathVariable Long backlog,
                                               @RequestBody SprintDTO dto){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        Sprint toSave = modelMapper.map(dto,Sprint.class);

        toSave.setBacklog(backlogObj);

        return sprintService.save(toSave)
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("projects/%d/backlogs/%d/sprints/%d".formatted(project,backlog,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, SprintDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{project}/backlogs/{backlog}/sprints")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SprintDTO>> getSprints(ModelMapper modelMapper,
                                                      @PathVariable Long project,
                                                      @PathVariable Long backlog,
                                                      @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);

        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(sprintService.findSprintsByBacklog(backlogObj, page, size).stream()
                .map(item -> modelMapper.map(item, SprintDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{project}/backlogs/{backlog}/sprints/{sprint}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintDTO> getSprintById(ModelMapper modelMapper,
                                                   @PathVariable Long project,
                                                   @PathVariable Long backlog,
                                                   @PathVariable Long sprint){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Sprint> optional = sprintService.findById(sprint);
        Sprint sprintObj = optional.orElse(null);

        if (!ControllerUtilities.checkSprintCoherence(projectObj,backlogObj,sprintObj))
            return ResponseEntity.notFound().build();


        return optional
                .stream()
                .map(found -> modelMapper.map(found, SprintDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{project}/backlogs/{backlog}/sprints/{sprint}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintDTO> editSprint(ModelMapper modelMapper,
                                                @PathVariable Long project,
                                                @PathVariable Long backlog,
                                                @PathVariable Long sprint,
                                                @RequestBody SprintDTO sprintDTO){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Sprint> optional = sprintService.findById(sprint);
        Sprint sprintObj = optional.orElse(null);

        if (!ControllerUtilities.checkSprintCoherence(projectObj,backlogObj,sprintObj))
            return ResponseEntity.notFound().build();


        return sprintService.update(sprint, modelMapper.map(sprintDTO, Sprint.class))
                .map(newDto -> modelMapper.map(newDto, SprintDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{project}/backlogs/{backlog}/sprints/{sprint}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteSprint(@PathVariable Long project, @PathVariable Long backlog,
                                                @PathVariable Long sprint){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Sprint> optional = sprintService.findById(sprint);
        Sprint sprintObj = optional.orElse(null);

        if (!ControllerUtilities.checkSprintCoherence(projectObj,backlogObj,sprintObj))
            return ResponseEntity.notFound().build();


        return optional
                .stream()
                .peek(sprintService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(x -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/insertions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintInsertionDTO> addItemToSprint(ModelMapper modelMapper,
                                                              @PathVariable Long project,
                                                              @PathVariable Long backlog,
                                                              @PathVariable Long sprint,
                                                              @RequestBody SprintInsertionDTO dto){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Sprint> optional = sprintService.findById(sprint);
        Sprint sprintObj = optional.orElse(null);

        if (!ControllerUtilities.checkSprintCoherence(projectObj,backlogObj,sprintObj))
            return ResponseEntity.notFound().build();

        if (sprintObj == null) {
            return ResponseEntity.notFound().build();
        }


        dto.setSprintId(sprintObj.getId());

        SprintInsertion toSave = modelMapper.map(dto, SprintInsertion.class);

        SprintInsertion saved = sprintInsertionService.save(toSave).orElse(null);
        SprintInsertionDTO dtoSaved = new SprintInsertionDTO();
        if (saved != null) {
            dtoSaved = modelMapper.map(saved, SprintInsertionDTO.class);
        }

        return ResponseEntity.created(URI.create("projects/%d/backlogs/%d/sprints/%d/insertions/%d"
                .formatted(project,backlog,sprint,dtoSaved.getId()))).body(dtoSaved);

    }

    @GetMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/insertions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SprintInsertionDTO>> insertionsOfASprint(ModelMapper modelMapper,
                                                                   @PathVariable Long project,
                                                                   @PathVariable Long backlog,
                                                                   @PathVariable Long sprint,
                                                                   @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Sprint> optional = sprintService.findById(sprint);
        Sprint sprintObj = optional.orElse(null);

        if (!ControllerUtilities.checkSprintCoherence(projectObj,backlogObj,sprintObj))
            return ResponseEntity.notFound().build();


        return ResponseEntity.ok(sprintInsertionService.findItemsBySprint(sprintObj, page, size).stream()
                .map(item -> modelMapper.map(item, SprintInsertionDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/insertions/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintInsertionDTO> itemOfASprint(ModelMapper modelMapper,
                                                            @PathVariable Long project,
                                                            @PathVariable Long backlog,
                                                            @PathVariable Long sprint,
                                                            @PathVariable Long item){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Sprint sprintObj =  sprintService.findById(sprint).orElse(null);
        Optional<SprintInsertion> optional = sprintInsertionService.findById(item);
        SprintInsertion sprintInsertionObj = optional.orElse(null);

        if (!ControllerUtilities.checkItemCoherenceInSprint(projectObj,backlogObj,sprintObj,sprintInsertionObj))
            return ResponseEntity.notFound().build();

        return optional
                .stream()
                .map(found -> modelMapper.map(found, SprintInsertionDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/insertions/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteSprintsItem(@PathVariable Long project,
                                                     @PathVariable Long backlog,
                                                     @PathVariable Long sprint,
                                                     @PathVariable Long item){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Sprint sprintObj =  sprintService.findById(sprint).orElse(null);
        Optional<SprintInsertion> optional = sprintInsertionService.findById(item);
        SprintInsertion sprintInsertionObj = optional.orElse(null);

        if (!ControllerUtilities.checkItemCoherenceInSprint(projectObj,backlogObj,sprintObj,sprintInsertionObj))
            return ResponseEntity.notFound().build();


        return optional
                .stream()
                .peek(sprintInsertionService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(x -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{project}/backlogs/{backlog}/roadmaps")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapDTO> addRoadmap(ModelMapper modelMapper,
                                                 @PathVariable Long project,
                                                 @PathVariable Long backlog,
                                                 @RequestBody RoadmapDTO dto){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        Roadmap toSave = modelMapper.map(dto,Roadmap.class);

        toSave.setBacklog(backlogObj);

        return roadmapService.save(toSave)
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("projects/%d/backlogs/%d/roadmaps/%d".formatted(project,backlog,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, RoadmapDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{project}/backlogs/{backlog}/roadmaps")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoadmapDTO>> getRoadmaps(ModelMapper modelMapper,
                                                        @PathVariable Long project,
                                                        @PathVariable Long backlog,
                                                        @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);

        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(roadmapService.findByBacklog(backlogObj, page, size).stream()
                .map(item -> modelMapper.map(item, RoadmapDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{project}/backlogs/{backlog}/roadmaps/{roadmap}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapDTO> getRoadmapById(ModelMapper modelMapper,
                                                     @PathVariable Long project,
                                                     @PathVariable Long backlog,
                                                     @PathVariable Long roadmap){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Roadmap> optional = roadmapService.findById(roadmap);
        Roadmap roadmapObj = optional.orElse(null);

        if (!ControllerUtilities.checkRoadmapCoherence(projectObj,backlogObj,roadmapObj))
            return ResponseEntity.notFound().build();


        return optional
                .stream()
                .map(found -> modelMapper.map(found, RoadmapDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{project}/backlogs/{backlog}/roadmaps/{roadmap}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteRoadmap(@PathVariable Long project, @PathVariable Long backlog,
                                                 @PathVariable Long roadmap){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Roadmap> optional = roadmapService.findById(roadmap);
        Roadmap roadmapObj = optional.orElse(null);

        if (!ControllerUtilities.checkRoadmapCoherence(projectObj,backlogObj,roadmapObj))
            return ResponseEntity.notFound().build();


        return optional
                .stream()
                .peek(roadmapService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(x -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{project}/backlogs/{backlog}/roadmaps/{roadmap}/insertions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapInsertionDTO> addItemToRoadmap(ModelMapper modelMapper,
                                                                @PathVariable Long project,
                                                                @PathVariable Long backlog,
                                                                @PathVariable Long roadmap,
                                                                @RequestBody RoadmapInsertionDTO dto){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Roadmap> optional = roadmapService.findById(roadmap);
        Roadmap roadmapObj = optional.orElse(null);

        if (!ControllerUtilities.checkRoadmapCoherence(projectObj,backlogObj,roadmapObj))
            return ResponseEntity.notFound().build();


        if (roadmapObj == null ) {
            return ResponseEntity.notFound().build();
        }

        dto.setRoadmapId(roadmapObj.getId());

        return roadmapInsertionService.save(modelMapper.map(dto, RoadmapInsertion.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("projects/%d/backlogs/%d/roadmaps/%d/insertions/%d"
                                .formatted(project,backlog,roadmap,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, RoadmapInsertionDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{project}/backlogs/{backlog}/roadmaps/{roadmap}/insertions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoadmapInsertionDTO>> insertionsOfARoadmap(ModelMapper modelMapper,
                                                                     @PathVariable Long project,
                                                                     @PathVariable Long backlog,
                                                                     @PathVariable Long roadmap,
                                                                     @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<Roadmap> optional = roadmapService.findById(roadmap);
        Roadmap roadmapObj = optional.orElse(null);

        if (!ControllerUtilities.checkRoadmapCoherence(projectObj,backlogObj,roadmapObj))
            return ResponseEntity.notFound().build();


        return ResponseEntity.ok(roadmapInsertionService.findAllByRoadmap(roadmapObj, page, size).stream()
                .map(item -> modelMapper.map(item, RoadmapInsertionDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{project}/backlogs/{backlog}/roadmaps/{roadmap}/insertions/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapInsertionDTO> itemOfARoadmap(ModelMapper modelMapper,
                                                              @PathVariable Long project,
                                                              @PathVariable Long backlog,
                                                              @PathVariable Long roadmap,
                                                              @PathVariable Long item){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Roadmap roadmapObj =  roadmapService.findById(roadmap).orElse(null);
        Optional<RoadmapInsertion> optional = roadmapInsertionService.findById(item);
        RoadmapInsertion roadmapInsertionObj = optional.orElse(null);

        if (!ControllerUtilities.checkItemCoherenceInRoadmap(projectObj,backlogObj,roadmapObj,roadmapInsertionObj))
            return ResponseEntity.notFound().build();

        return optional
                .stream()
                .map(found -> modelMapper.map(found, RoadmapInsertionDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{project}/backlogs/{backlog}/roadmaps/{roadmap}/insertions/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapInsertionDTO> editRoadmapsItem(ModelMapper modelMapper,
                                                                @PathVariable Long project,
                                                                @PathVariable Long backlog,
                                                                @PathVariable Long roadmap,
                                                                @PathVariable Long item,
                                                                @RequestBody RoadmapInsertionDTO roadmapInsertionDTO){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Roadmap roadmapObj =  roadmapService.findById(roadmap).orElse(null);
        Optional<RoadmapInsertion> optional = roadmapInsertionService.findById(item);
        RoadmapInsertion roadmapInsertionObj = optional.orElse(null);

        if (roadmapInsertionObj == null) {
            return ResponseEntity.notFound().build();
        }

        roadmapInsertionDTO.setId(roadmapInsertionObj.getId());
        roadmapInsertionDTO.setRoadmapId(roadmap);

        if (!ControllerUtilities.checkItemCoherenceInRoadmap(projectObj,backlogObj,roadmapObj,roadmapInsertionObj))
            return ResponseEntity.notFound().build();

        return roadmapInsertionService.update(item, modelMapper.map(roadmapInsertionDTO, RoadmapInsertion.class))
                .map(newDto -> modelMapper.map(newDto, RoadmapInsertionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{project}/backlogs/{backlog}/roadmaps/{roadmap}/insertions/{insertion}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteRoadmapsinsertion(@PathVariable Long project,
                                                      @PathVariable Long backlog,
                                                      @PathVariable Long roadmap,
                                                      @PathVariable Long insertion){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Roadmap roadmapObj =  roadmapService.findById(roadmap).orElse(null);
        Optional<RoadmapInsertion> optional = roadmapInsertionService.findById(insertion);
        RoadmapInsertion roadmapInsertionObj = optional.orElse(null);

        if (!ControllerUtilities.checkItemCoherenceInRoadmap(projectObj,backlogObj,roadmapObj,roadmapInsertionObj))
            return ResponseEntity.notFound().build();


        return optional
                .stream()
                .peek(roadmapInsertionService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(x -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }



    @GetMapping ("/{project}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> itemsOfTheProject(ModelMapper modelMapper, @PathVariable Long project,
                                                        @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        return ResponseEntity.ok(itemService.findAllByProjectNoFather(projectService.findById(project).orElse(null), page,size)
                .stream()
                .map(p -> modelMapper.map(p, ItemDTO.class))
                .collect(Collectors.toList()));
    }


    @GetMapping ("/{project}/backlogs/{backlog}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> itemsOfTheBacklog(ModelMapper modelMapper, @PathVariable Long project, @PathVariable Long backlog,
                                                           @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        Project projectObj = projectService.findById(project).orElse(null);
        ProductBacklog backlogObj = backlogService.findById(backlog).orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(itemService.findAllByBacklogNoFather(backlogObj, page,size)
                .stream()
                .map(p -> modelMapper.map(p, ItemDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping ("/{project}/backlogs/{backlog}/sprints/{sprint}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> itemsOfTheSprint(ModelMapper modelMapper, @PathVariable Long project, @PathVariable Long backlog, @PathVariable Long sprint,
                                                           @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        Project projectObj = projectService.findById(project).orElse(null);
        ProductBacklog backlogObj = backlogService.findById(backlog).orElse(null);
        Sprint sprintObj = sprintService.findById(sprint).orElse(null);

        if (!ControllerUtilities.checkSprintCoherence(projectObj,backlogObj,sprintObj)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(itemService.findAllBySprintNoFather(sprintObj, page,size)
                .stream()
                .map(p -> modelMapper.map(p, ItemDTO.class))
                .collect(Collectors.toList()));
    }


    @GetMapping ("/{project}/backlogs/{backlog}/roadmaps/{roadmap}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> itemsOfTheRoadmap(ModelMapper modelMapper, @PathVariable Long project, @PathVariable Long backlog, @PathVariable Long roadmap,
                                                          @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        Project projectObj = projectService.findById(project).orElse(null);
        ProductBacklog backlogObj = backlogService.findById(backlog).orElse(null);
        Roadmap roadmapObj = roadmapService.findById(roadmap).orElse(null);

        if (!ControllerUtilities.checkRoadmapCoherence(projectObj,backlogObj,roadmapObj)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(itemService.finAllByRoadmapNoFather(roadmapObj, page,size)
                .stream()
                .map(p -> modelMapper.map(p, ItemDTO.class))
                .collect(Collectors.toList()));

    @GetMapping("/{project}/sprint")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintDTO> getActiveSprint(ModelMapper modelMapper, @PathVariable Long project) {

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<Sprint> optional = sprintService.findActiveSprint(projectObj);
        Sprint sprintObj = optional.orElse(null);

        if (!ControllerUtilities.checkSprintValidity(projectObj, sprintObj))
            return ResponseEntity.notFound().build();


        return optional
                .stream()
                .map(found -> modelMapper.map(found, SprintDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

}

