package it.unical.unijira.services.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.unical.unijira.data.models.TokenType;

import java.util.Map;

public interface AuthService {
    String authenticate(String username, String password);
    String refresh(String token) throws JWTVerificationException;
    DecodedJWT verifyToken(String token, TokenType type, String... requiredClaims) throws JWTVerificationException;
    String generateToken(TokenType type, Map<String, ?> claims);
}
