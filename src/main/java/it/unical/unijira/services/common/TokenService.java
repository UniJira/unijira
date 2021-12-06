package it.unical.unijira.services.common;

import it.unical.unijira.data.models.TokenType;

import java.util.Map;
import java.util.Optional;


public interface TokenService {

    boolean isNotValid(String token);
    boolean isExpired(String token);
    TokenType getType(String token);
    Optional<String> getPayload(String token, String claim);

    String generate(TokenType type, Map<String, String> claims);

}
