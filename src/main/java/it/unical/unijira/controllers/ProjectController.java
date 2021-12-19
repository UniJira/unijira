package it.unical.unijira.controllers;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.MembershipDTO;
import it.unical.unijira.data.dto.ProjectDTO;
import it.unical.unijira.data.dto.user.ProductBacklogDTO;
import it.unical.unijira.data.dto.user.ProductBacklogInsertionDTO;
import it.unical.unijira.data.dto.user.SprintDTO;
import it.unical.unijira.data.dto.user.SprintInsertionDTO;
import it.unical.unijira.data.models.*;
import it.unical.unijira.services.common.*;
import it.unical.unijira.utils.ControllerUtilities;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ProjectService projectService;
    private final ProductBacklogService backlogService;
    private final ProductBacklogInsertionService insertionService;
    private final ItemService pbiService;
    private final SprintService sprintService;
    private final SprintInsertionService sprintInsertionService;
    private final RoadmapService roadmapService;
    private final RoadmapInsertionService roadmapInsertionService;
    @Autowired
    public ProjectController(ProjectService projectService,
                             ProductBacklogService backlogService,
                             ProductBacklogInsertionService insertionService,
                             ItemService pbiService,
                             SprintService sprintService,
                             SprintInsertionService sprintInsertionService,
                             RoadmapService roadmapService,
                             RoadmapInsertionService roadmapInsertionService) {
        this.projectService = projectService;
        this.backlogService = backlogService;
        this.insertionService = insertionService;
        this.pbiService = pbiService;
        this.sprintService = sprintService;
        this.sprintInsertionService = sprintInsertionService;
        this.roadmapService = roadmapService;
        this.roadmapInsertionService = roadmapInsertionService;
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

    @GetMapping("/{project}/backlogs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogDTO>> readAllBacklogs(ModelMapper modelMapper, @PathVariable Long project,
                                                                   Integer page, Integer size) {

        Project projectObj = projectService.findById(project).orElse(null);

        if (!ControllerUtilities.checkProjectValidity(projectObj)) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(backlogService.findAllByProject(projectObj).stream()
                .map(item -> modelMapper.map(item, ProductBacklogDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{project}/backlogs/{backlog}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> readBacklog(ModelMapper modelMapper, @PathVariable Long project,
                                                         @PathVariable Long backlog) {

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
    public ResponseEntity<ProductBacklogDTO> createBacklog(ModelMapper modelMapper,  @PathVariable Long project,
                                                           @RequestBody ProductBacklogDTO dto) {

        Project projectObj = projectService.findById(project).orElse(null);

        if (!ControllerUtilities.checkProjectValidity(projectObj)) {
            return ResponseEntity.notFound().build();
        }

        dto.setProject(modelMapper.map(projectObj, ProjectDTO.class));

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

    @PostMapping("/{project}/backlogs/{backlog}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> addItemToBacklog(ModelMapper modelMapper,
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
                        .created(URI.create("projects/%d/backlogs/%d/items/%d"
                                .formatted(project,backlog, createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ProductBacklogInsertionDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{project}/backlogs/{backlog}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogInsertionDTO>> allFromBacklog(ModelMapper modelMapper,
                                                                           @PathVariable Long project,
                                                                           @PathVariable Long backlog){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(insertionService.findAllByBacklog(backlogObj).stream()
                .map(insertion -> modelMapper.map(insertion, ProductBacklogInsertionDTO.class))
                .collect(Collectors.toList()));

    }

    @GetMapping("/{project}/backlogs/{backlog}/items/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> insertionById(ModelMapper modelMapper,
                                                                    @PathVariable Long project,
                                                                    @PathVariable Long backlog,
                                                                    @PathVariable Long item){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Optional<ProductBacklogInsertion> optional = insertionService.findById(item);
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

    @PutMapping("/{project}/backlogs/{backlog}/items/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> updateInsertion(ModelMapper modelMapper, @PathVariable Long project,
                                                                      @PathVariable Long backlog, @PathVariable Long item,
                                                                      @RequestBody ProductBacklogInsertionDTO insertionDTO){

        Project projectObj = projectService.findById(project).orElse(null);
        ProductBacklogInsertion pbi = insertionService.findById(item).orElse(null);
        ProductBacklog backlogObj = backlogService.findById(backlog).orElse(null);

        if (pbi == null || pbi.getItem() == null ||
                !ControllerUtilities.checkItemCoherence(projectObj,backlogObj,pbi.getItem(),pbi)) {
            return ResponseEntity.notFound().build();
        }

        return insertionService.update(item, modelMapper.map(insertionDTO, ProductBacklogInsertion.class))
                .map(newDto -> modelMapper.map(newDto, ProductBacklogInsertionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{project}/backlogs/{backlog}/items/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteInsertion(@PathVariable Long project, @PathVariable Long backlog,
                                                   @PathVariable Long item) {


        Optional<ProductBacklogInsertion> optional = insertionService.findById(item);
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

        dto.setBacklog(modelMapper.map(backlogObj, ProductBacklogDTO.class));

        return sprintService.save(modelMapper.map(dto, Sprint.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("projects/%d/backlogs/%d/sprints/%d".formatted(project,backlog,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, SprintDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{project}/backlogs/{backlog}/sprints")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SprintDTO>> getSprints(ModelMapper modelMapper,
                                                      @PathVariable Long project,
                                                      @PathVariable Long backlog){


        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);

        if (!ControllerUtilities.checkProjectCoherence(projectObj,backlogObj)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(sprintService.findSprintsByBacklog(backlogObj).stream()
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


    @PostMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/items")
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


        dto.setSprint(modelMapper.map(sprintObj, SprintDTO.class));

        return sprintInsertionService.save(modelMapper.map(dto, SprintInsertion.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("projects/%d/backlogs/%d/sprints/%d/items/%d"
                                .formatted(project,backlog,sprint,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, SprintInsertionDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SprintInsertionDTO>> itemsOfASprint(ModelMapper modelMapper,
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


        return ResponseEntity.ok(sprintInsertionService.findItemsBySprint(sprintObj).stream()
                .map(item -> modelMapper.map(item, SprintInsertionDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/items/{item}")
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


    @PutMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/items/{item}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintInsertionDTO> editSprintsItem(ModelMapper modelMapper,
                                                              @PathVariable Long project,
                                                              @PathVariable Long backlog,
                                                              @PathVariable Long sprint,
                                                              @PathVariable Long item,
                                                              @RequestBody SprintInsertionDTO sprintDTO){

        Project projectObj = projectService.findById(project).orElse(null);
        Optional<ProductBacklog> backlogOpt = backlogService.findById(backlog);
        ProductBacklog backlogObj = backlogOpt.orElse(null);
        Sprint sprintObj =  sprintService.findById(sprint).orElse(null);
        Optional<SprintInsertion> optional = sprintInsertionService.findById(item);
        SprintInsertion sprintInsertionObj = optional.orElse(null);

        if (!ControllerUtilities.checkItemCoherenceInSprint(projectObj,backlogObj,sprintObj,sprintInsertionObj))
            return ResponseEntity.notFound().build();

        return sprintInsertionService.update(item, modelMapper.map(sprintDTO, SprintInsertion.class))
                .map(newDto -> modelMapper.map(newDto, SprintInsertionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{project}/backlogs/{backlog}/sprints/{sprint}/items/{item}")
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
}


//  TODO Fare i test
//  TODO Paginazione per i get all
