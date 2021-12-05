package it.unical.unijira.utils.filters;

import com.auth0.jwt.JWT;
import it.unical.unijira.data.models.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final var authorization = request.getHeader("Authorization");

        if(authorization != null && authorization.startsWith("Bearer ")) {

            final var jwt = JWT.decode(authorization.substring(7));

            if(jwt.getExpiresAt().after(Date.from(Instant.now()))) {

                if(jwt.getClaim("type").asString().equals(Token.TokenType.AUTHORIZATION.name())) {

                    try {

                        SecurityContextHolder.getContext()
                                .setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                        jwt.getClaim("username").asString(),
                                        jwt.getClaim("password").asString()
                                )));

                    } catch (AuthenticationException e) {
                        LOGGER.error("Invalid credentials with token {} from {}: {} {}, {}", authorization, request.getRemoteAddr(), jwt.getClaim("username").asString(), jwt.getClaim("password").asString(), e);
                    } catch (Exception e) {
                        LOGGER.error("Unexpected error with token {} from {}: {}", authorization, request.getRemoteAddr(), e);
                    }

                } else {

                    LOGGER.error("Invalid token type {} from {}", jwt.getClaim("type").asString(), request.getRemoteAddr());

                }

            } else {

                LOGGER.error("Token expired with token {} from {}", authorization, request.getRemoteAddr());

            }


        } else {

            LOGGER.error("Missing authorization token from {}", request.getRemoteAddr());

        }


        filterChain.doFilter(request, response);

    }
}
