package it.unical.unijira.controllers;

import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.auth.AuthenticationRequest;
import it.unical.unijira.services.auth.impl.AuthServiceImpl;
import it.unical.unijira.services.impl.TokenServiceImpl;
import it.unical.unijira.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;


@RestController
@RequestMapping("/auth")
public record AuthController(AuthServiceImpl authService, UserServiceImpl userService, TokenServiceImpl tokenService) {

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


    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody User user) {

        if(user.getUsername().isBlank())
            return ResponseEntity.badRequest().build();

        if(user.getPassword().isBlank())
            return ResponseEntity.badRequest().build();


        if(userService().findByUsername(user.getUsername()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();



        return userService.save(user)
                .<ResponseEntity<Void>>map (
                        v  -> ResponseEntity.created(URI.create("/users/" + v.getId())).build())
                .orElseGet (
                        () -> ResponseEntity.badRequest().build()
                );

    }


    @GetMapping("active")
    public ResponseEntity<Void> register(@RequestParam String tokenId) {

        if(!tokenService.check(tokenId))
            return ResponseEntity.badRequest().build();

        if(tokenService.find(tokenId).isEmpty())
            return ResponseEntity.notFound().build();

        userService.active(tokenService.find(tokenId).get().getUser());
        return ResponseEntity.ok().build();

    }


}
