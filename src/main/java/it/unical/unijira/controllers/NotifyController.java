package it.unical.unijira.controllers;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.NotifyDTO;
import it.unical.unijira.services.common.NotifyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/notifies")
public class NotifyController implements CrudController<NotifyDTO, Long> {

    private final NotifyService notifyService;

    @Autowired
    public NotifyController(NotifyService notifyService) {
        this.notifyService = notifyService;
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotifyDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {

        return ResponseEntity.ok(notifyService
                .findAllByUserId(getAuthenticatedUser().getId(), page, size)
                .stream()
                .map(notify -> modelMapper.map(notify, NotifyDTO.class))
                .collect(Collectors.toList()));

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotifyDTO> read(ModelMapper modelMapper, Long id) {

        return notifyService.findById(id)
                .stream()
                .filter(notify -> notify.getUser().getId().equals(getAuthenticatedUser().getId()))
                .peek(notify -> notifyService.markAsRead(id))
                .map(notify -> modelMapper.map(notify, NotifyDTO.class))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Override
    public ResponseEntity<NotifyDTO> create(ModelMapper modelMapper, NotifyDTO dto) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @Override
    public ResponseEntity<NotifyDTO> update(ModelMapper modelMapper, Long id, NotifyDTO dto) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
