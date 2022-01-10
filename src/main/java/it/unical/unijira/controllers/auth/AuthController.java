package it.unical.unijira.controllers.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import it.unical.unijira.data.dto.user.*;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.auth.AuthUserDetails;
import it.unical.unijira.services.common.UserService;
import it.unical.unijira.utils.RegexUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(ModelMapper modelMapper, AuthService authService, UserService userService) {
        this.modelMapper = modelMapper;
        this.authService = authService;
        this.userService = userService;
    }

    
    @PostMapping("authenticate")
    public ResponseEntity<String> authenticate (@RequestBody UserAuthenticationDTO userAuthenticationDTO) {

        if(!StringUtils.hasText(userAuthenticationDTO.getUsername()))
            return ResponseEntity.badRequest().build();

        if(!StringUtils.hasText(userAuthenticationDTO.getPassword()))
            return ResponseEntity.badRequest().build();

        try {

            return ResponseEntity.ok(authService.authenticate(userAuthenticationDTO.getUsername(), userAuthenticationDTO.getPassword()));

        } catch (AuthenticationException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @PostMapping("refresh")
    public ResponseEntity<String> refresh(@RequestBody UserExpiredTokenDTO userExpiredTokenDTO) {

        if(!StringUtils.hasText(userExpiredTokenDTO.getToken()))
            return ResponseEntity.badRequest().build();

        try {

            return ResponseEntity.ok(authService.refresh(userExpiredTokenDTO.getToken()));

        } catch (AuthenticationException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @PostMapping("register")
    public ResponseEntity<UserInfoDTO> register(ModelMapper modelMapper, @RequestBody UserRegisterDTO user) {

        if(!StringUtils.hasText(user.getUsername()))
            return ResponseEntity.badRequest().build();

        if(!StringUtils.hasText(user.getPassword()))
            return ResponseEntity.badRequest().build();


        if(userService.findByUsername(user.getUsername()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();


        return userService.save(modelMapper.map(user, User.class))
                .map (
                        v  -> ResponseEntity
                                .created(URI.create("/users/%d".formatted(v.getId())))
                                .body(modelMapper.map(v, UserInfoDTO.class)))
                .orElseGet (
                        () -> ResponseEntity.badRequest().build()
                );

    }


    @GetMapping("available")
    public ResponseEntity<Boolean> isUserAvailable(@RequestParam String username) {

        if(!StringUtils.hasText(username))
            return ResponseEntity.badRequest().build();

        if(userService.findByUsername(username).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.ok(true);

    }


    @GetMapping("active")
    public ResponseEntity<Boolean> active(@RequestParam String token) {

        if(!StringUtils.hasText(token))
            return ResponseEntity.badRequest().build();

        try {

            var decoded = authService.verifyToken(token, TokenType.ACCOUNT_CONFIRM, "userId");

            return Optional.ofNullable(decoded.getClaim("userId").asLong())
                    .map(userService::activate)
                    .map(v -> ResponseEntity.ok(true))
                    .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }


    @PostMapping("password-reset-with-token")
    public ResponseEntity<Boolean> passwordResetWithToken(Authentication authentication, @RequestBody UserPasswordResetDTO userPasswordResetDTO) {


        if(!StringUtils.hasText(userPasswordResetDTO.getToken()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if(!StringUtils.hasText(userPasswordResetDTO.getPassword()))
            return ResponseEntity.badRequest().build();

        if(!RegexUtils.isValidPassword(userPasswordResetDTO.getPassword()))
            return ResponseEntity.badRequest().build();


        Long userId = null;

        try {

            userId = authService
                    .verifyToken(userPasswordResetDTO.getToken(), TokenType.ACCOUNT_RESET_PASSWORD, "userId")
                    .getClaim("userId")
                    .asLong();

        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE).build();

        } catch (JWTVerificationException e) {
            LOGGER.trace("resetPassword(): WARN! JWTVerificationException on TokenType.ACCOUNT_RESET_PASSWORD: {}", e.getMessage());
        }


        try {

            if(userId == null) {

                var decoded = authService.verifyToken(userPasswordResetDTO.getToken(), TokenType.PROJECT_INVITE, "userId", "reset");

                if(decoded.getClaim("reset").asBoolean())
                    userId = decoded.getClaim("userId").asLong();

            }

        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE).build();

        } catch (JWTVerificationException e) {
            LOGGER.trace("resetPassword(): WARN! JWTVerificationException on TokenType.PROJECT_INVITE: {}", e.getMessage());
        }


        if(userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


        return userService.resetPassword(userId, userPasswordResetDTO.getPassword())
                .map(v -> ResponseEntity.ok(true))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }


    @PostMapping("password-reset")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> passwordReset(Authentication authentication, @RequestBody UserPasswordResetDTO userPasswordResetDTO) {


        if(!StringUtils.hasText(userPasswordResetDTO.getPassword()))
            return ResponseEntity.badRequest().build();

        if(!RegexUtils.isValidPassword(userPasswordResetDTO.getPassword()))
            return ResponseEntity.badRequest().build();

        if(authentication == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(authentication.getPrincipal() == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


        if(authentication.getPrincipal() instanceof AuthUserDetails userDetails) {

            return userService.resetPassword(userDetails.getModel().getId(), userPasswordResetDTO.getPassword())
                    .map(v -> ResponseEntity.ok(true))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        }


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }


    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfoDTO> me(Authentication authentication) {

        if(authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if(!(authentication.getPrincipal() instanceof AuthUserDetails))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return userService.findById(((AuthUserDetails) authentication.getPrincipal()).getModel().getId())
                .map(user -> modelMapper.map(user, UserInfoDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());

    }


}
