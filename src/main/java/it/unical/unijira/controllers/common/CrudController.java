package it.unical.unijira.controllers.common;

import it.unical.unijira.data.models.User;
import it.unical.unijira.services.auth.AuthUserDetails;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface CrudController<T, S> {

    @GetMapping("")
    ResponseEntity<List<T>> readAll (
            ModelMapper modelMapper,
            @RequestParam (required = false, defaultValue = "0") Integer page,
            @RequestParam (required = false, defaultValue = "10000") Integer size
    );

    @GetMapping("{id}")
    ResponseEntity<T> read(ModelMapper modelMapper, @PathVariable S id);

    @PostMapping("")
    ResponseEntity<T> create(ModelMapper modelMapper, @RequestBody T dto);

    @PutMapping("{id}")
    ResponseEntity<T> update(ModelMapper modelMapper, @PathVariable S id, @RequestBody T dto);

    @DeleteMapping("{id}")
    ResponseEntity<Void> delete(@PathVariable S id);



    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    default void handleAuthenticationCredentialsNotFoundException() {
        LoggerFactory.getLogger(CrudController.class).error("Error during CRUD Request: Authentication not found");
    }

    default User getAuthenticatedUser() {

        if(SecurityContextHolder.getContext().getAuthentication() == null)
            throw new AuthenticationCredentialsNotFoundException("Authentication not found");

        if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthUserDetails))
            throw new AuthenticationCredentialsNotFoundException("Authentication Details not found");

        return ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getModel();

    }

}
