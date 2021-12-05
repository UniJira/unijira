package it.unical.unijira.services.auth.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.models.Token;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.auth.AuthUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final String tokenSecret;
    private final Integer tokenExpiration;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
            @Value("${jwt.secret}") String tokenSecret,
            @Value("${jwt.expiration}") Integer tokenExpiration) {

        this.authenticationManager = authenticationManager;
        this.tokenSecret = tokenSecret;
        this.tokenExpiration = tokenExpiration;

    }


    @Override
    public String authenticate(String username, String password) throws AuthenticationException {

        SecurityContextHolder.getContext()
                .setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)));

        LOGGER.debug("Authentication successful for user {}", username);


        if(SecurityContextHolder.getContext().getAuthentication() == null)
            throw new SecurityException("User not authenticated: missing authentication");

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null)
            throw new SecurityException("User not authenticated: missing details");


        var userDetails = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("type", Token.TokenType.AUTHORIZATION.name())
                .withClaim("username", username)
                .withClaim("password", password)
                .withExpiresAt(Date.from(LocalDateTime.now().plusSeconds(tokenExpiration).toInstant(java.time.ZoneOffset.UTC)))
                .sign(Algorithm.HMAC512(tokenSecret));


    }

    @Override
    public void logout(String token) {
        SecurityContextHolder.clearContext();
    }

}
