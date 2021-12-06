package it.unical.unijira.services.common.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.services.common.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final String tokenSecret;
    private final Long tokenExpiration;

    @Autowired
    public TokenServiceImpl(
            @Value("${jwt.secret}") String tokenSecret,
            @Value("${jwt.expiration}") Long tokenExpiration) {

        this.tokenSecret = tokenSecret;
        this.tokenExpiration = tokenExpiration;

    }


    @Override
    public boolean isNotValid(String token) {
        try {
            JWT.decode(token);
            return false;
        } catch (JWTDecodeException e) {
            return true;
        }

    }

    @Override
    public boolean isExpired(String token) {
        return JWT.decode(token).getExpiresAt().before(Date.from(Instant.now()));
    }

    @Override
    public TokenType getType(String token) {
        return JWT.decode(token).getClaim("type").as(TokenType.class);
    }

    @Override
    public Optional<String> getPayload(String token, String claim) {
        return Optional.of(JWT.decode(token).getClaim(claim).asString());
    }

    @Override
    public String generate(TokenType type, Map<String, String> claims) {

        var jwt = JWT.create()
                .withSubject(UUID.randomUUID().toString())
                .withIssuer("unijira")
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(tokenExpiration)))
                .withClaim("type", type.name());

        for (var claim : claims.entrySet())
            jwt.withClaim(claim.getKey(), claim.getValue());

        return jwt.sign(Algorithm.HMAC256(tokenSecret));

    }

}
