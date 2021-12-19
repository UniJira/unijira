package it.unical.unijira.controllers;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.NotifyDTO;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.services.common.NotifyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/notifications")
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
                .findFirst()
                .map(notify -> modelMapper.map(notify, NotifyDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Override
    public ResponseEntity<NotifyDTO> create(ModelMapper modelMapper, NotifyDTO dto) {

        return notifyService.create(modelMapper.map(dto, Notify.class))
                .map(notify -> ResponseEntity
                        .created(URI.create("/notifications/%d".formatted(notify.getId())))
                        .body(modelMapper.map(notify, NotifyDTO.class)))
                .orElse(ResponseEntity.badRequest().build());

    }

    @Override
    public ResponseEntity<NotifyDTO> update(ModelMapper modelMapper, Long id, NotifyDTO dto) {

        return notifyService.update(id, modelMapper.map(dto, Notify.class))
                .map(notify -> modelMapper.map(notify, NotifyDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Override
    public ResponseEntity<Boolean> delete(Long id) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }


    @PutMapping("/mark")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> markAllAsRead() {

        return notifyService.findAllByUserId(getAuthenticatedUser().getId())
                .stream()
                .peek(notify -> notify.setRead(true))
                .map(notify -> notifyService.update(notify.getId(), notify))
                .map(notify -> ResponseEntity.ok(true))
                .reduce((a, b) -> ResponseEntity.ok(true))
                .orElse(ResponseEntity.notFound().build());

    }

}
