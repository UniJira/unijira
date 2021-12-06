package it.unical.unijira.services.auth;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

@Getter
public class AuthTokenException extends ServletException {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthTokenException.class);

    private final HttpStatus httpStatus;
    private final String message;

    public AuthTokenException(HttpStatus httpStatus, String message) {

        this.httpStatus = httpStatus;
        this.message = message;

        LOGGER.error("Authentication error (%s): %s".formatted(httpStatus.name(), message));

    }

}
