package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.TokenRepository;
import it.unical.unijira.data.models.Token;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public record TokenServiceImpl(TokenRepository tokenRepository) implements TokenService {

    @Autowired
    public TokenServiceImpl {}

    @Override
    public String generate(User owner, Token.TokenType type, @Nullable LocalDateTime expireDate) {

        if(expireDate == null)
            expireDate = LocalDateTime.now().plusDays(1);

        if(expireDate.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Expire date must be in the future");


        final var token = new Token();
        token.setId(UUID.randomUUID().toString());
        token.setUser(owner);
        token.setTokenType(type);
        token.setExpireDate(expireDate);

        return tokenRepository.saveAndFlush(token).getId();

    }

    @Override
    public boolean check(String tokenId) {

        return tokenRepository.findById(tokenId)
                .map(i -> i.getExpireDate().isAfter(LocalDateTime.now()))
                .orElse(false);

    }


    @Override
    public Optional<Token> find(String tokenId) {
        return tokenRepository.findById(tokenId);
    }


}
