package it.unical.unijira.controllers.auth;

import it.unical.unijira.data.dto.user.UserAuthenticationDTO;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import it.unical.unijira.data.dto.user.UserRegisterDTO;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.auth.AuthUserDetails;
import it.unical.unijira.services.common.TokenService;
import it.unical.unijira.services.common.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final TokenService tokenService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(ModelMapper modelMapper, AuthService authService, UserService userService, TokenService tokenService) {
        this.modelMapper = modelMapper;
        this.authService = authService;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    
    @PostMapping("authenticate")
    public ResponseEntity<String> authenticate (@RequestBody UserAuthenticationDTO userAuthenticationDTO) {

        if(userAuthenticationDTO.getUsername().isBlank())
            return ResponseEntity.badRequest().build();

        if(userAuthenticationDTO.getPassword().isBlank())
            return ResponseEntity.badRequest().build();

        try {

            return ResponseEntity.ok(authService.authenticate(userAuthenticationDTO.getUsername(), userAuthenticationDTO.getPassword()));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO user) {

        if(user.getUsername().isBlank())
            return ResponseEntity.badRequest().build();

        if(user.getPassword().isBlank())
            return ResponseEntity.badRequest().build();


        if(userService.findByUsername(user.getUsername()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();


        return userService.save(modelMapper.map(user, User.class))
                .map (
                        v  -> ResponseEntity.created(URI.create("")).body(""))
                .orElseGet (
                        () -> ResponseEntity.badRequest().build()
                );

    }


    @GetMapping("active")
    public ResponseEntity<Boolean> active(@RequestParam String token) {

        if(tokenService.isNotValid(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if(tokenService.isExpired(token))
            return ResponseEntity.status(HttpStatus.GONE).build();

        if(tokenService.getType(token) != TokenType.ACCOUNT_CONFIRM)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return tokenService.getPayload(token, "userId")
                .map(Long::valueOf)
                .map(userService::activate)
                .map(v -> ResponseEntity.ok(true))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    }


    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfoDTO> me(Authentication authentication) {

        if(authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if(!(authentication.getPrincipal() instanceof AuthUserDetails))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(modelMapper.map(((AuthUserDetails) authentication.getPrincipal()).getModel(), UserInfoDTO.class));

    }


}
