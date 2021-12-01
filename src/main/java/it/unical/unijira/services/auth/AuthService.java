package it.unical.unijira.services.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public record AuthService(AuthenticationManager authenticationManager) {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService {}


    public boolean authenticate(String username, String password) {

        try {

            SecurityContextHolder.getContext()
                    .setAuthentication(authenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password)));

        } catch (AuthenticationException e) {

            LOGGER.error("Authentication failed for user {}", username);
            return false;

        }

        LOGGER.debug("Authentication successful for user {}", username);
        return true;

    }

    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

}
