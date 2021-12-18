package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.ProductBacklogDTO;
import it.unical.unijira.data.dto.user.ProductBacklogInsertionDTO;
import it.unical.unijira.data.dto.user.SprintDTO;
import it.unical.unijira.data.dto.user.SprintInsertionDTO;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.SprintInsertion;
import it.unical.unijira.services.common.ProductBacklogInsertionService;
import it.unical.unijira.services.common.ProductBacklogService;
import it.unical.unijira.services.common.SprintInsertionService;
import it.unical.unijira.services.common.SprintService;
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
@RequestMapping("/backlog")
public class ProductBacklogController implements CrudController<ProductBacklogDTO, Long> {


    private final ProductBacklogService backlogService;
    private final ProductBacklogInsertionService insertionService;
    private final SprintService sprintService;
    private final SprintInsertionService sprintInsertionService;

    @Autowired
    public ProductBacklogController(ProductBacklogService backlogService,
                                    ProductBacklogInsertionService insertionService,
                                    SprintService sprintService,
                                    SprintInsertionService sprintInsertionService) {
        this.backlogService = backlogService;
        this.insertionService = insertionService;
        this.sprintService = sprintService;
        this.sprintInsertionService = sprintInsertionService;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        return ResponseEntity.ok(backlogService.findAll().stream()
                .map(item -> modelMapper.map(item, ProductBacklogDTO.class))
                .collect(Collectors.toList()));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> read(ModelMapper modelMapper, Long id) {

        return backlogService.findById(id)
                .stream()
                .map(backlog -> modelMapper.map(backlog, ProductBacklogDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> create(ModelMapper modelMapper, ProductBacklogDTO dto) {

        return backlogService.save(modelMapper.map(dto, ProductBacklog.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/backlog/%d".formatted(createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ProductBacklogDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> update(ModelMapper modelMapper, Long id, ProductBacklogDTO dto) {
        return backlogService.update(id, modelMapper.map(dto, ProductBacklog.class))
                .map(newDto -> modelMapper.map(newDto, ProductBacklogDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> delete(Long id) {
        return backlogService.findById(id)
                .stream()
                .peek(backlogService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(project -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{backlog}/item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> addInsertionToBacklog(ModelMapper modelMapper,
                                                                            @PathVariable Long backlog,
                                                                            @RequestBody ProductBacklogInsertionDTO dto){

        ProductBacklog pb = backlogService.findById(backlog).get();
        dto.setBacklog(modelMapper.map(pb, ProductBacklogDTO.class));
        return insertionService.save(modelMapper.map(dto, ProductBacklogInsertion.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/backlog/%d/insertion/%d".formatted(backlog, createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ProductBacklogInsertionDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{backlog}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogInsertionDTO>> allFromBacklog(ModelMapper modelMapper,
                                                                            @PathVariable Long backlog){
        ProductBacklog pb = backlogService.findById(backlog).get();
        if (pb == null ) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(insertionService.findAllByBacklog(pb).stream()
                .map(insertion -> modelMapper.map(insertion, ProductBacklogInsertionDTO.class))
                .collect(Collectors.toList()));

    }

    @PutMapping("/{backlog}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> updateInsertion(ModelMapper modelMapper,
                                                          @PathVariable Long backlog, @PathVariable Long id,
                                                          @RequestBody ProductBacklogInsertionDTO insertionDTO){

        ProductBacklogInsertion pbi = insertionService.findById(id).get();
        ProductBacklog backlogObj = backlogService.findById(backlog).get();
        if (pbi == null || backlogObj == null ) {
            return ResponseEntity.notFound().build();
        }
        if (pbi.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }
        return insertionService.update(id, modelMapper.map(insertionDTO, ProductBacklogInsertion.class))
                .map(newDto -> modelMapper.map(newDto, ProductBacklogInsertionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{backlog}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> insertionById(ModelMapper modelMapper,
                                                                      @PathVariable Long backlog,
                                                                      @PathVariable Long id){

        Optional<ProductBacklogInsertion> optional = insertionService.findById(id);
        ProductBacklogInsertion pbi = optional.get();

        if (pbi.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return optional
                .stream()
                .map(found -> modelMapper.map(found, ProductBacklogInsertionDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{backlog}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteInsertion(ModelMapper modelMapper,
                                                   @PathVariable Long backlog,
                                                   @PathVariable Long id){


        Optional<ProductBacklogInsertion> optional = insertionService.findById(id);
        ProductBacklogInsertion pbi = optional.get();

        if (pbi.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return optional
                .stream()
                .peek(insertionService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(project -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{backlog}/sprint")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintDTO> addSprint(ModelMapper modelMapper,
                                                           @PathVariable Long backlog,
                                                           @RequestBody SprintDTO dto){


        ProductBacklog backlogObj = backlogService.findById(backlog).get();
        dto.setBacklog(modelMapper.map(backlogObj, ProductBacklogDTO.class));

        return sprintService.save(modelMapper.map(dto, Sprint.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/backlog/%d/sprint/%d".formatted(backlog,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, SprintDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{backlog}/sprints")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SprintDTO>> getSprints(ModelMapper modelMapper,
                                               @PathVariable Long backlog){

        ProductBacklog backlogObj = backlogService.findById(backlog).get();

        return ResponseEntity.ok(sprintService.findSprintsByBacklog(backlogObj).stream()
                .map(item -> modelMapper.map(item, SprintDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{backlog}/sprint/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintDTO> getSprintById(ModelMapper modelMapper,
                                               @PathVariable Long backlog,
                                               @PathVariable Long id){


        Optional<Sprint> optional = sprintService.findById(id);
        Sprint sprint = optional.get();

        if (sprint.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return optional
                .stream()
                .map(found -> modelMapper.map(found, SprintDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{backlog}/sprint/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintDTO> editSprint(ModelMapper modelMapper,
                                               @PathVariable Long backlog,
                                               @PathVariable Long id,
                                               @RequestBody SprintDTO sprintDTO){


        Sprint sprint = sprintService.findById(id).get();
        ProductBacklog backlogObj = backlogService.findById(backlog).get();
        if (sprint == null || backlogObj == null ) {
            return ResponseEntity.notFound().build();
        }
        if (sprint.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }
        return sprintService.update(id, modelMapper.map(sprintDTO, Sprint.class))
                .map(newDto -> modelMapper.map(newDto, SprintDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{backlog}/sprint/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteSprint(ModelMapper modelMapper,
                                                   @PathVariable Long backlog,
                                                   @PathVariable Long id){
        Optional<Sprint> optional = sprintService.findById(id);
        Sprint sprint = optional.get();

        if (sprint.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return optional
                .stream()
                .peek(sprintService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(project -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{backlog}/sprint/{sprint}/item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintInsertionDTO> addItemToSprint(ModelMapper modelMapper,
                                               @PathVariable Long backlog,
                                               @PathVariable Long sprint,
                                               @RequestBody SprintInsertionDTO dto){


        Sprint sprintObj = sprintService.findById(sprint).get();
        if (sprintObj.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }
        dto.setSprint(modelMapper.map(sprintObj, SprintDTO.class));

        return sprintInsertionService.save(modelMapper.map(dto, SprintInsertion.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/backlog/%d/sprint/%d/item/%d".formatted(backlog,sprint,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, SprintInsertionDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{backlog}/sprint/{sprint}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SprintInsertionDTO>> itemsOfASprint(ModelMapper modelMapper,
                                               @PathVariable Long backlog,
                                               @PathVariable Long sprint){


        Sprint sprintObj = sprintService.findById(sprint).get();

        if (sprintObj.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(sprintInsertionService.findItemsBySprint(sprintObj).stream()
                .map(item -> modelMapper.map(item, SprintInsertionDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{backlog}/sprint/{sprint}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintInsertionDTO> itemOfASprint(ModelMapper modelMapper,
                                                             @PathVariable Long backlog,
                                                             @PathVariable Long sprint,
                                                             @PathVariable Long id){

        Sprint sprintObj = sprintService.findById(sprint).get();

        Optional<SprintInsertion> optional = sprintInsertionService.findById(id);
        SprintInsertion insertion = optional.get();
        if (insertion.getSprint().getId() != sprint ||
            sprintObj.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return optional
                .stream()
                .map(found -> modelMapper.map(found, SprintInsertionDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{backlog}/sprint/{sprint}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintInsertionDTO> editSprintsItem(ModelMapper modelMapper,
                                                @PathVariable Long backlog,
                                                @PathVariable Long sprint,
                                                @PathVariable Long id,
                                                @RequestBody SprintInsertionDTO sprintDTO){

        SprintInsertion toEdit = sprintInsertionService.findById(id).get();

        Sprint sprintObj = sprintService.findById(sprint).get();
        ProductBacklog backlogObj = backlogService.findById(backlog).get();
        if (id == null || toEdit != null) {
            return ResponseEntity.notFound().build();
        }
        if (backlogObj == null || sprintObj != null || backlogObj.getId() != sprintObj.getBacklog().getId()
                || toEdit.getSprint().getId() != sprintObj.getId()) {
            return ResponseEntity.badRequest().build();
        }
        return sprintInsertionService.update(id, modelMapper.map(sprintDTO, SprintInsertion.class))
                .map(newDto -> modelMapper.map(newDto, SprintInsertionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{backlog}/sprint/{sprint}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteSrpintsItem(ModelMapper modelMapper,
                                                     @PathVariable Long backlog,
                                                     @PathVariable Long sprint,
                                                     @PathVariable Long id){
        Optional<SprintInsertion> optional = sprintInsertionService.findById(id);
        SprintInsertion insertion = optional.get();
        Sprint sprintObj = sprintService.findById(sprint).get();
        if (insertion.getSprint().getId() != sprint || sprintObj.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return optional
                .stream()
                .peek(sprintInsertionService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(project -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


}
