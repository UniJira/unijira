package it.unical.unijira.services;

import it.unical.unijira.data.models.Token;
import it.unical.unijira.data.models.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenService {
    Optional<Token> find(String tokenId);
    String generate(User owner, Token.TokenType type, LocalDateTime expireDate);
    boolean check(String tokenId);
}
