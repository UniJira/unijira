package it.unical.unijira.controllers;

import it.unical.unijira.controllers.common.CrudController;
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


    @Autowired
    public UsersController(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public ResponseEntity<List<UserInfoDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {

        return ResponseEntity.ok(userService.findAll(page, size)
                .stream()
                .map(user -> modelMapper.map(user, UserInfoDTO.class))
                .collect(Collectors.toList()));

    }



    @Override
    public ResponseEntity<UserInfoDTO> read(ModelMapper modelMapper, Long id) {

        return userService.findById(id)
                .map(user -> modelMapper.map(user, UserInfoDTO.class))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Override
    public ResponseEntity<UserInfoDTO> create(ModelMapper modelMapper, UserInfoDTO dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<UserInfoDTO> update(ModelMapper modelMapper, Long id, UserInfoDTO dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Boolean> delete(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @GetMapping("/{id}/tickets/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> getTickets(ModelMapper modelMapper, @PathVariable Long id, @PathVariable ItemStatus status, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size) {

        if(User.CURRENT_USER_ID.equals(id))
            id = getAuthenticatedUser().getId();

        return ResponseEntity.ok(itemService.findAllByUser(id, page, size).stream()
                .filter(item -> status.equals(item.getStatus()))
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .toList());

    }

    @GetMapping("/{id}/collaborators")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserInfoDTO>> getCollaborators(ModelMapper modelMapper, @PathVariable Long id) {

        User me = userService.findById(id).orElse(null);

        return ResponseEntity.ok(userService.getCollaborators(me)
                .stream()
                .map(user -> modelMapper.map(user, UserInfoDTO.class))
                .collect(Collectors.toList()));
    }



}
