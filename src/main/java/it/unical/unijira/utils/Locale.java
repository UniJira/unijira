package it.unical.unijira.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("application")
public class Locale {

    private final static Logger LOGGER = LoggerFactory.getLogger(Locale.class);

    private final String language;
    private final Map<String, String> dictionary;


    @Autowired
    public Locale(@Value("${platform.locale.language}") String language) throws IOException {

        this.language = language;

        try(final var stream = Locale.class.getClassLoader().getResourceAsStream("locale/%s.json".formatted(language))) {

            if(stream == null)
                throw new IOException("Unable to find locale file for language %s".formatted(language));


            LOGGER.info("Loading locale file for language {}", language);

            final var mapper = new ObjectMapper();
            final var type = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);

            this.dictionary = mapper.readValue(stream, type);


        }


    }


    public String getLanguage() {
        return language;
    }

    public String get(String key) {
        return dictionary.get(key);
    }

}
