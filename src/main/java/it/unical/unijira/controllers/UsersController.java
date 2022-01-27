package it.unical.unijira.controllers;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.ProjectDTO;
import it.unical.unijira.data.dto.items.ItemDTO;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.items.ItemStatus;
import it.unical.unijira.services.common.ItemService;
import it.unical.unijira.services.common.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users")
public class UsersController implements CrudController<UserInfoDTO, Long> {

    private final UserService userService;
    private final ItemService itemService;
    private final ModelMapper modelMapper;


    @Autowired
    public UsersController(UserService userService, ItemService itemService, ModelMapper modelMapper) {
        this.userService = userService;
        this.itemService = itemService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<List<UserInfoDTO>> readAll(Integer page, Integer size) {

        return ResponseEntity.ok(userService.findAll(page, size)
                .stream()
                .map(user -> modelMapper.map(user, UserInfoDTO.class))
                .collect(Collectors.toList()));

    }



    @Override
    public ResponseEntity<UserInfoDTO> read(Long id) {

        return userService.findById(id)
                .map(user -> modelMapper.map(user, UserInfoDTO.class))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Override
    public ResponseEntity<UserInfoDTO> create(UserInfoDTO dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<UserInfoDTO> update(Long id, UserInfoDTO dto) {

        System.err.println(dto.getAvatar() !=null ? dto.getAvatar().toString() : "NULL");

        if(User.CURRENT_USER_ID.equals(id))
            id = getAuthenticatedUser().getId();

        return userService.update(id, modelMapper.map(dto, User.class))
                .map(newDto -> modelMapper.map(newDto, UserInfoDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Boolean> delete(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @GetMapping("/me/tickets/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> getTickets(@PathVariable ItemStatus status, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        return ResponseEntity.ok(itemService.findAllByUser(getAuthenticatedUser().getId(), page, size).stream()
                .filter(item -> status.equals(item.getStatus()))
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .toList());

    }

    @GetMapping("/{id}/collaborators")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserInfoDTO>> getCollaborators(@PathVariable Long id) {

        User me = userService.findById(id).orElse(null);

        return ResponseEntity.ok(userService.getCollaborators(me)
                .stream()
                .map(user -> modelMapper.map(user, UserInfoDTO.class))
                .collect(Collectors.toList()));
    }


    @GetMapping("/{id}/projects")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDTO>> getProjects(@PathVariable Long id) {

        User me = userService.findById(id).orElse(null);

        return ResponseEntity.ok(userService.getProjects(me)
                .stream()
                .map(user -> modelMapper.map(user, ProjectDTO.class))
                .collect(Collectors.toList()));
    }



}
