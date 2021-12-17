package it.unical.unijira.services.auth.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final Config config;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, Config config) {

        this.authenticationManager = authenticationManager;
        this.config = config;

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


        return generateToken(TokenType.AUTHORIZATION, Map.of(
                "username", username,
                "password", password
        ));

    }

    @Override
    public String refresh(String token) throws JWTVerificationException {

        var decoded = JWT.require(config.getJWTAlgorithm())
                .withIssuer(config.getJWTIssuer())
                .withClaim("type", TokenType.AUTHORIZATION.name())
                .withClaimPresence("username")
                .withClaimPresence("password")
                .acceptLeeway(config.getTokenLeeway())
                .build()
                .verify(token);

        return authenticate(decoded.getClaim("username").asString(), decoded.getClaim("password").asString());

    }


    @Override
    public DecodedJWT verifyToken(String token, TokenType type, String... requiredClaims) throws JWTVerificationException {

        var verifier = JWT.require(config.getJWTAlgorithm())
                .withIssuer(config.getJWTIssuer())
                .withClaim("type", type.name());

        for(var claim : requiredClaims)
            verifier = verifier.withClaimPresence(claim);

        return verifier.build().verify(token);

    }


    @Override
    public String generateToken(TokenType type, Map<String, String> claims) {

        return JWT.create()
                .withIssuer(config.getJWTIssuer())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(config.getTokenExpiration())))
                .withClaim("type", type.name())
                .withPayload(claims)
                .sign(config.getJWTAlgorithm());

    }
}
