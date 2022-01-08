package it.unical.unijira.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);


    private final AuthenticationManager authenticationManager;
    private final Config config;

    public AuthTokenFilter(AuthenticationManager authenticationManager, Config config) {
        this.authenticationManager = authenticationManager;
        this.config = config;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(Arrays.asList(config.getPublicUrls()).contains(request.getRequestURI())) {

            filterChain.doFilter(request, response);

        } else {

            try {

                //! FIXME: Use HttpOnly cookies

                if (request.getHeader("Authorization") == null)
                    throw new AuthTokenException(HttpStatus.FORBIDDEN, "Authorization header is null");

                if (!request.getHeader("Authorization").startsWith("Bearer "))
                    throw new AuthTokenException(HttpStatus.FORBIDDEN, "Authorization header is not valid: %s".formatted(request.getHeader("Authorization")));



                final var authorization = request.getHeader("Authorization").substring(7);

                if(!StringUtils.hasText(authorization))
                    throw new AuthTokenException(HttpStatus.FORBIDDEN, "Authorization token is empty");



                try {

                    var decoded = JWT.require(config.getJWTAlgorithm())
                            .withIssuer(config.getJWTIssuer())
                            .withClaim("type", TokenType.AUTHORIZATION.name())
                            .withClaimPresence("username")
                            .withClaimPresence("password")
                            .build()
                            .verify(authorization);


                    try {


                        SecurityContextHolder.getContext()
                                .setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                        decoded.getClaim("username").asString(),
                                        decoded.getClaim("password").asString()
                                )));


                        filterChain.doFilter(request, response);
                        return;


                    } catch (AuthenticationException e) {
                        LOGGER.trace("Invalid credentials with token {} from {} <{}>, {}", authorization, request.getRemoteAddr(), decoded.getClaim("username").asString(), e.getMessage());
                    } catch (Exception e) {
                        LOGGER.error("Unexpected error with token {} from {}: {}", authorization, request.getRemoteAddr(), e.getMessage());
                    }


                    throw new AuthTokenException(HttpStatus.FORBIDDEN, "Authentication failed with token %s".formatted(authorization));


                } catch (TokenExpiredException e) {
                    throw new AuthTokenException(HttpStatus.I_AM_A_TEAPOT, "Token expired %s".formatted(authorization));
                } catch (Exception e) {
                    throw new AuthTokenException(HttpStatus.FORBIDDEN, "Invalid authorization token %s: %s".formatted(authorization, e));
                }



            } catch (AuthTokenException e) {

                request.setAttribute("auth-token-exception", e);
                response.sendError(e.getHttpStatus().value(), e.getMessage());

            }

        }

    }


}
