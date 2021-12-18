package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.RoadmapDTO;
import it.unical.unijira.data.dto.user.RoadmapInsertionDTO;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import it.unical.unijira.services.common.RoadmapInsertionService;
import it.unical.unijira.services.common.RoadmapService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController implements CrudController<RoadmapDTO, Long> {

    private final RoadmapService roadmapService;
    private final RoadmapInsertionService insertionService;

    @Autowired
    public RoadmapController(RoadmapService roadmapService,
                             RoadmapInsertionService insertionService) {
        this.roadmapService = roadmapService;
        this.insertionService = insertionService;
    }

    @Override
    public ResponseEntity<List<RoadmapDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        return ResponseEntity.ok(roadmapService.findAll().stream()
                .map(roadmap -> modelMapper.map(roadmap, RoadmapDTO.class))
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<RoadmapDTO> read(ModelMapper modelMapper, Long id) {
        return roadmapService.findById(id)
                .stream()
                .map(roadmap -> modelMapper.map(roadmap, RoadmapDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<RoadmapDTO> create(ModelMapper modelMapper, RoadmapDTO dto) {
        return roadmapService.save(modelMapper.map(dto, Roadmap.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/roadmap/%d".formatted(createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, RoadmapDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<RoadmapDTO> update(ModelMapper modelMapper, Long id, RoadmapDTO dto) {
        return roadmapService.update(id, modelMapper.map(dto, Roadmap.class))
                .map(newDto -> modelMapper.map(newDto, RoadmapDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Boolean> delete(Long id) {

        return roadmapService.findById(id)
                .stream()
                .peek(roadmapService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(object -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roadmap}/item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapInsertionDTO> addInsertionToRoadmap(ModelMapper modelMapper,
                                                                     @PathVariable Long roadmap,
                                                                     @RequestBody RoadmapInsertionDTO dto){
        Roadmap roadmapObj = roadmapService.findById(roadmap).get();
        dto.setRoadmap(modelMapper.map(roadmapObj, RoadmapDTO.class));
        return insertionService.save(modelMapper.map(dto, RoadmapInsertion.class))
                .map(createdDTO -> ResponseEntity.created(
                        URI.create("/roadmap/%d/insertion/%d".formatted(roadmap,createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, RoadmapInsertionDTO.class)))
                .orElse(ResponseEntity.badRequest().build());

    }

    @GetMapping("/{roadmap}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoadmapInsertionDTO>> allFromRoadmap(ModelMapper modelMapper,
                                                                           @PathVariable Long roadmap){

        Roadmap roadmapObj = roadmapService.findById(roadmap).get();
        if (roadmapObj == null ) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(insertionService.findAllByRoadmap(roadmapObj).stream()
                .map(insertion -> modelMapper.map(insertion, RoadmapInsertionDTO.class))
                .collect(Collectors.toList()));
    }


    @PutMapping("/{roadmap}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapInsertionDTO> updateInsertion(ModelMapper modelMapper,
                                                                      @PathVariable Long roadmap, @PathVariable Long id,
                                                                      @RequestBody RoadmapInsertionDTO insertionDTO){

        RoadmapInsertion insertion = insertionService.findById(id).get();
        Roadmap roadmapObj = roadmapService.findById(roadmap).get();
        if (insertion == null || roadmapObj == null ) {
            return ResponseEntity.notFound().build();
        }
        if (insertion.getRoadmap().getId() != roadmap) {
            return ResponseEntity.badRequest().build();
        }
        return insertionService.update(id, modelMapper.map(insertionDTO, RoadmapInsertion.class))
                .map(newDto -> modelMapper.map(newDto, RoadmapInsertionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{roadmap}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoadmapInsertionDTO> insertionById(ModelMapper modelMapper,
                                                                    @PathVariable Long roadmap,
                                                                    @PathVariable Long id){
        RoadmapInsertion roadmapInsertion = insertionService.findById(id).get();

        if (roadmapInsertion.getRoadmap().getId() != roadmap) {
            return ResponseEntity.badRequest().build();
        }

        return insertionService.findById(id)
                .stream()
                .map(found -> modelMapper.map(found, RoadmapInsertionDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{roadmap}/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteInsertion(@PathVariable Long roadmap, @PathVariable Long id){

        RoadmapInsertion roadmapInsertion = insertionService.findById(id).get();

        if (roadmapInsertion.getRoadmap().getId() != roadmap) {
            return ResponseEntity.badRequest().build();
        }

        return insertionService.findById(id)
                .stream()
                .peek(insertionService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(object -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }
}
