package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.ProductBacklogDTO;
import it.unical.unijira.data.dto.user.ProductBacklogInsertionDTO;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.services.common.ProductBacklogInsertionService;
import it.unical.unijira.services.common.ProductBacklogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/backlog")
public class ProductBacklogController implements CrudController<ProductBacklogDTO, Long> {


    private final ProductBacklogService backlogService;
    private final ProductBacklogInsertionService insertionService;

    @Autowired
    public ProductBacklogController(ProductBacklogService backlogService,
                                    ProductBacklogInsertionService insertionService) {
        this.backlogService = backlogService;
        this.insertionService = insertionService;
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


    @PostMapping("/{backlog}/insertion")
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

    @GetMapping("/{backlog}/insertions")
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

    @PutMapping("/{backlog}/insertion/{id}")
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

    @GetMapping("/{backlog}/insertion/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogInsertionDTO> insertionById(ModelMapper modelMapper,
                                                                      @PathVariable Long backlog,
                                                                      @PathVariable Long id){


        ProductBacklogInsertion pbi = insertionService.findById(id).get();

        if (pbi.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return insertionService.findById(id)
                .stream()
                .map(found -> modelMapper.map(found, ProductBacklogInsertionDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{backlog}/insertion/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteInsertion(ModelMapper modelMapper,
                                                   @PathVariable Long backlog,
                                                   @PathVariable Long id){


        ProductBacklogInsertion pbi = insertionService.findById(id).get();

        if (pbi.getBacklog().getId() != backlog) {
            return ResponseEntity.badRequest().build();
        }

        return insertionService.findById(id)
                .stream()
                .peek(insertionService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(project -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

}
