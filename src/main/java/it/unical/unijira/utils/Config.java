package it.unical.unijira.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("application")
@Getter
public class Config {

    private final String baseURL;
    private final String locale;

    @Autowired
    public Config(
            @Value("${config.baseURL}") String baseURL,
            @Value("${config.locale}") String locale
    ) {

        this.baseURL = baseURL;
        this.locale = locale;

    }
}
