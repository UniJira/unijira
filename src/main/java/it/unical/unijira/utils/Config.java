package it.unical.unijira.utils;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@Scope("application")
public class Config {

    private final String baseURL;
    private final String environment;
    private final String locale;
    private final String tokenSecret;
    private final Integer tokenExpiration;
    private final Integer tokenLeeway;
    private final String[] publicUrls;

    @Autowired
    public Config(
            @Value("${config.baseURL}") String baseURL,
            @Value("${config.environment}") String environment,
            @Value("${config.locale}") String locale,
            @Value("${config.jwt.secret}") String tokenSecret,
            @Value("${config.jwt.expiration}") Integer tokenExpiration,
            @Value("${config.jwt.leeway}") Integer tokenLeeway,
            @Value("${config.routes.public}") String publicUrls
    ) {

        this.baseURL = baseURL;
        this.environment = environment;
        this.locale = locale;
        this.tokenSecret = tokenSecret;
        this.tokenExpiration = tokenExpiration;
        this.tokenLeeway = tokenLeeway;
        this.publicUrls = publicUrls.split(";");

    }

    public Algorithm getJWTAlgorithm() {
        return Algorithm.HMAC256(tokenSecret);
    }

    public String getJWTIssuer() {
        return "auth-service-unijira";
    }

}
