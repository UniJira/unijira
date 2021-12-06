package it.unical.unijira.services.auth;

import com.auth0.jwt.JWT;
import it.unical.unijira.data.models.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);


    private final AuthenticationManager authenticationManager;
    private final List<String> publicUrls;

    public AuthTokenFilter(AuthenticationManager authenticationManager, List<String> publicUrls) {
        this.authenticationManager = authenticationManager;
        this.publicUrls = publicUrls;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(publicUrls.stream().anyMatch(request.getRequestURI()::equals)) {

            filterChain.doFilter(request, response);

        } else {

            try {

                if (request.getHeader("Authorization") == null)
                    throw new AuthTokenException(HttpStatus.UNAUTHORIZED, "Invalid authorization type");

                if (!request.getHeader("Authorization").startsWith("Bearer "))
                    throw new AuthTokenException(HttpStatus.UNAUTHORIZED, "Invalid authorization type");


                final var authorization = request.getHeader("Authorization").substring(7);
                final var jwt = JWT.decode(authorization);

                if (jwt.getExpiresAt().before(Date.from(Instant.now())))
                    throw new AuthTokenException(HttpStatus.I_AM_A_TEAPOT, "Token expired %s".formatted(authorization));

                if (!jwt.getClaim("type").asString().equals(TokenType.AUTHORIZATION.name()))
                    throw new AuthTokenException(HttpStatus.UNAUTHORIZED, "Invalid authorization token %s".formatted(authorization));


                try {


                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                    jwt.getClaim("username").asString(),
                                    jwt.getClaim("password").asString()
                            )));

                    filterChain.doFilter(request, response);
                    return;


                } catch (AuthenticationException e) {
                    LOGGER.error("Invalid credentials with token {} from {}: {} {}, {}", authorization, request.getRemoteAddr(), jwt.getClaim("username").asString(), jwt.getClaim("password").asString(), e);
                } catch (Exception e) {
                    LOGGER.error("Unexpected error with token {} from {}: {}", authorization, request.getRemoteAddr(), e);
                }


                throw new AuthTokenException(HttpStatus.UNAUTHORIZED, "Authentication failed with token %s".formatted(authorization));


            } catch (AuthTokenException e) {

                request.setAttribute("auth-token-exception", e);
                response.sendError(e.getHttpStatus().value(), e.getMessage());

            }

        }

    }


}
