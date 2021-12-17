package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.ProductBacklogItemDTO;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.services.common.NoteService;
import it.unical.unijira.services.common.ProductBacklogItemService;
import it.unical.unijira.utils.ProductBacklogItemType;
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
@RequestMapping("/backlog/items")
public class ProductBacklogItemController implements CrudController<ProductBacklogItemDTO, Long> {

    private final ProductBacklogItemService pbiService;
    private final NoteService noteService;

    @Autowired
    public ProductBacklogItemController(ProductBacklogItemService pbiService, NoteService noteService) {
        this.pbiService = pbiService;
        this.noteService = noteService;
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogItemDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        return ResponseEntity.ok(pbiService.findAll().stream()
                        .map(item -> modelMapper.map(item, ProductBacklogItemDTO.class))
                                .collect(Collectors.toList()));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogItemDTO> read(ModelMapper modelMapper, Long id) {
        return pbiService.findById(id)
                .stream()
                .map(item -> modelMapper.map(item, ProductBacklogItemDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogItemDTO> create(ModelMapper modelMapper, ProductBacklogItemDTO itemDto) {

        if(itemDto.getSummary().isBlank())
            return ResponseEntity.badRequest().build();
        if(itemDto.getDescription().isBlank())
            return ResponseEntity.badRequest().build();
        if(itemDto.getType().isBlank() ||
               ! ProductBacklogItemType.getInstance().isCoherentType(itemDto.getType()))
            return ResponseEntity.badRequest().build();

        return pbiService.save(modelMapper.map(itemDto, ProductBacklogItem.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/backlog/items/%d".formatted(createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ProductBacklogItemDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogItemDTO> update(ModelMapper modelMapper, Long id, ProductBacklogItemDTO dto) {
        return pbiService.update(id, modelMapper.map(dto, ProductBacklogItem.class))
                .map(newDto -> modelMapper.map(newDto, ProductBacklogItemDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> delete(Long id) {
        return pbiService.findById(id)
                .stream()
                .peek(pbiService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(project -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("by_user/{user}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogItemDTO>> itemsByUser(ModelMapper modelMapper, @PathVariable Long user, Integer page, Integer size) {
        return ResponseEntity.ok(pbiService.findAllByUser(user, page, size).stream()
                .map(item -> modelMapper.map(item, ProductBacklogItemDTO.class))
                .collect(Collectors.toList()));

    }

    @GetMapping("by_father/{father}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogItemDTO>> itemsByFather(ModelMapper modelMapper, @PathVariable Long father, Integer page, Integer size){
        return ResponseEntity.ok(pbiService.findAllByFather(father, page, size).stream()
                .map(item -> modelMapper.map(item, ProductBacklogItemDTO.class))
                .collect(Collectors.toList()));
    }

    /* TODO Notes management
    @PostMapping("{item}/note")
    public ResponseEntity<ProductBacklogItemDTO> addNote(ModelMapper modelMapper, @PathVariable Long item, @RequestBody NoteDTO note) {

    }


    @GetMapping("{item}/notes")
    public ResponseEntity<List<ProductBacklogItemDTO>> getNotes(ModelMapper modelMapper, Long item, Integer page, Integer size) {

    }

     @PutMapping("{item}/note/{note}")
     public ResponseEntity<ProductBacklogItemDTO> getNoteById(ModelMapper modelMapper, @PathVariable Long item, @PathVariable Long note) {

     }

    @PutMapping("{item}/note/{note}")
    public ResponseEntity<ProductBacklogItemDTO> updateNote(ModelMapper modelMapper, @RequestBody NoteDTO note) {

    }


    @PutMapping("{item}/note/{note}")
    public ResponseEntity<ProductBacklogItemDTO> deleteNote(@PathVariable Long item, @PathVariable Long note) {

    }
    */

}
