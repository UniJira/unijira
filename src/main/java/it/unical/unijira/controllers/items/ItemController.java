package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.items.ItemAssignmentDTO;
import it.unical.unijira.data.dto.items.ItemDTO;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemAssignment;
import it.unical.unijira.services.common.ItemAssignmentService;
import it.unical.unijira.services.common.ItemService;
import it.unical.unijira.services.common.NoteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController implements CrudController<ItemDTO, Long> {

    private final ItemService pbiService;
    private final NoteService noteService;
    private final ItemAssignmentService itemAssignmentService;
    private final ModelMapper modelMapper;

    @Autowired
    public ItemController(ItemService pbiService, NoteService noteService, ItemAssignmentService itemAssignmentService, ModelMapper modelMapper) {
        this.pbiService = pbiService;
        this.noteService = noteService;
        this.itemAssignmentService = itemAssignmentService;
        this.modelMapper = modelMapper;
    }



    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> readAll(Integer page, Integer size) {
        return ResponseEntity.ok(pbiService.findAll().stream()
                        .map(item -> modelMapper.map(item, ItemDTO.class))
                                .collect(Collectors.toList()));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemDTO> read(Long id) {
        return pbiService.findById(id)
                .stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemDTO> create(ItemDTO itemDto) {

        if(!StringUtils.hasText(itemDto.getSummary()))
            return ResponseEntity.badRequest().build();

        if(!StringUtils.hasText(itemDto.getDescription()))
            return ResponseEntity.badRequest().build();


         Item toSave = modelMapper.map(itemDto, Item.class);
        if (toSave.getFather() == null && itemDto.getFatherId() != null) {
            try {
                toSave.setFather(pbiService.findById(itemDto.getFatherId()).orElse(null));
            }
            catch (NonValidItemTypeException e ) {
                return ResponseEntity.badRequest().build();
            }
        }


        return pbiService.save(toSave)
                .map(createdDTO -> ResponseEntity
                        .created(URI.create("/items/%d".formatted(createdDTO.getId())))
                        .body(modelMapper.map(createdDTO, ItemDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemDTO> update(Long id, ItemDTO dto) {
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
    public ResponseEntity<List<ItemDTO>> itemsByUser(@PathVariable Long user, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {
        return ResponseEntity.ok(pbiService.findAllByUser(user, page, size).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList()));

    }

    @GetMapping("by-father/{father}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> itemsByFather(@PathVariable Long father, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {
        return ResponseEntity.ok(pbiService.findAllByFather(father, page, size).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList()));
    }


    @GetMapping("{itemId}/assignments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemAssignmentDTO>> readAllAssignments(
                                                  @RequestParam (required = false, defaultValue = "0") Integer page,
                                                  @RequestParam (required = false, defaultValue = "10000") Integer size,
                                                  @PathVariable Long itemId) {

        Item item = pbiService.findById(itemId).orElse(null);

        if(item == null ) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(itemAssignmentService.findAllByItem(item,page,size).stream()
                .map(topic -> modelMapper.map(topic, ItemAssignmentDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("{itemId}/assignments/{assignmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemAssignmentDTO> readAssignment(@PathVariable Long itemId,
                                         @PathVariable Long assignmentId) {


        return itemAssignmentService.findByIdAndItem(assignmentId, itemId)
                .map(assignment -> modelMapper.map(assignment, ItemAssignmentDTO.class))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("{itemId}/assignments")
    public ResponseEntity<ItemAssignmentDTO> createAssignment(@RequestBody ItemAssignmentDTO dto,
                                           @PathVariable Long itemId) {
        if (dto.getItemId() == null) {
            dto.setItemId(itemId);
        }
        else if (!dto.getItemId().equals(itemId)) {
            return ResponseEntity.badRequest().build();
        }

        return itemAssignmentService.save(modelMapper.map(dto, ItemAssignment.class))
                .map(savedObject -> ResponseEntity
                        .created(URI.create("/items/%d/assignments/%d".formatted(itemId,savedObject.getId())))
                        .body(modelMapper.map(savedObject, ItemAssignmentDTO.class))).orElse(ResponseEntity.badRequest().build());
    }


    @PutMapping("{itemId}/assignments/{assignmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemAssignmentDTO> updateAssignment(@RequestBody ItemAssignmentDTO dto,
                                           @PathVariable Long itemId,
                                           @PathVariable Long assignmentId) {

        if (dto.getItemId() == null || !dto.getItemId().equals(itemId)) {
            return ResponseEntity.badRequest().build();
        }

        return itemAssignmentService.update(modelMapper.map(dto,ItemAssignment.class),assignmentId)
                .map(updated -> modelMapper.map(updated, ItemAssignmentDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("{itemId}/assignments/{assignmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteAssignment(@PathVariable Long itemId,
                                          @PathVariable Long assignmentId) {

        return itemAssignmentService.findByIdAndItem(assignmentId, itemId)
                .stream()
                .peek(itemAssignmentService::delete)
                .findFirst()
                .<ResponseEntity<Boolean>>map(x -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


    /* TODO Notes management
    @PostMapping("{item}/notes")
    public ResponseEntity<ProductBacklogItemDTO> addNote(@PathVariable Long item, @RequestBody NoteDTO note) {

    }


    @GetMapping("{item}/notes")
    public ResponseEntity<List<ProductBacklogItemDTO>> getNotes(Long item, Integer page, Integer size) {

    }

     @PutMapping("{item}/notes/{note}")
     public ResponseEntity<ProductBacklogItemDTO> getNoteById(@PathVariable Long item, @PathVariable Long note) {

     }

    @PutMapping("{item}/notes/{note}")
    public ResponseEntity<ProductBacklogItemDTO> updateNote(@RequestBody NoteDTO note) {

    }


    @PutMapping("{item}/notes/{note}")
    public ResponseEntity<ProductBacklogItemDTO> deleteNote(@PathVariable Long item, @PathVariable Long note) {

    }
    */

}
