package it.unical.unijira.controllers;

import it.unical.unijira.data.models.auth.AuthenticationRequest;
import it.unical.unijira.services.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/auth")
public record AuthController(AuthService authService) {

    @Autowired
    public AuthController {}


    @PostMapping("authenticate")
    public ResponseEntity<Void> authenticate (HttpServletRequest request, @RequestBody AuthenticationRequest authenticationRequest) {

        if(authenticationRequest.username().isBlank())
            return ResponseEntity.badRequest().build();

        if(authenticationRequest.password().isBlank())
            return ResponseEntity.badRequest().build();

        if(!authService().authenticate(authenticationRequest.username(), authenticationRequest.password()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok().build();

    }


}
