package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.items.ItemDTO;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.services.common.ItemService;
import it.unical.unijira.services.common.NoteService;
import it.unical.unijira.utils.ItemType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController implements CrudController<ItemDTO, Long> {

    private final ItemService pbiService;
    private final NoteService noteService;

    @Autowired
    public ItemController(ItemService pbiService, NoteService noteService) {
        this.pbiService = pbiService;
        this.noteService = noteService;
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        return ResponseEntity.ok(pbiService.findAll().stream()
                        .map(item -> modelMapper.map(item, ItemDTO.class))
                                .collect(Collectors.toList()));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemDTO> read(ModelMapper modelMapper, Long id) {
        return pbiService.findById(id)
                .stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemDTO> create(ModelMapper modelMapper, ItemDTO itemDto) {

        if(itemDto.getSummary().isBlank())
            return ResponseEntity.badRequest().build();
        if(itemDto.getDescription().isBlank())
            return ResponseEntity.badRequest().build();
        if(itemDto.getType().isBlank() ||
               ! ItemType.getInstance().isCoherentType(itemDto.getType()))
            return ResponseEntity.badRequest().build();

        return pbiService.save(modelMapper.map(itemDto, Item.class))
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/items/%d".formatted(createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ItemDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemDTO> update(ModelMapper modelMapper, Long id, ItemDTO dto) {
        return pbiService.update(id, modelMapper.map(dto, Item.class))
                .map(newDto -> modelMapper.map(newDto, ItemDTO.class))
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


    @GetMapping("by-user/{user}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> itemsByUser(ModelMapper modelMapper, @PathVariable Long user, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {
        return ResponseEntity.ok(pbiService.findAllByUser(user, page, size).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList()));

    }

    @GetMapping("by-father/{father}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> itemsByFather(ModelMapper modelMapper, @PathVariable Long father, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {
        return ResponseEntity.ok(pbiService.findAllByFather(father, page, size).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList()));
    }

    /* TODO Notes management
    @PostMapping("{item}/notes")
    public ResponseEntity<ProductBacklogItemDTO> addNote(ModelMapper modelMapper, @PathVariable Long item, @RequestBody NoteDTO note) {

    }


    @GetMapping("{item}/notes")
    public ResponseEntity<List<ProductBacklogItemDTO>> getNotes(ModelMapper modelMapper, Long item, Integer page, Integer size) {

    }

     @PutMapping("{item}/notes/{note}")
     public ResponseEntity<ProductBacklogItemDTO> getNoteById(ModelMapper modelMapper, @PathVariable Long item, @PathVariable Long note) {

     }

    @PutMapping("{item}/notes/{note}")
    public ResponseEntity<ProductBacklogItemDTO> updateNote(ModelMapper modelMapper, @RequestBody NoteDTO note) {

    }


    @PutMapping("{item}/notes/{note}")
    public ResponseEntity<ProductBacklogItemDTO> deleteNote(@PathVariable Long item, @PathVariable Long note) {

    }
    */

}
