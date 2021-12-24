package it.unical.unijira.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Scope("application")
public class Locale {

    private final static Logger LOGGER = LoggerFactory.getLogger(Locale.class);


    private final Map<String, String> dictionary;


    @Autowired
    public Locale(Config config) throws IOException {

        try(final var stream = Resources.getResourceAsStream("locale/%s.json".formatted(config.getLocale()))) {

            if(stream == null)
                throw new IOException("Unable to find locale file for language %s".formatted(config.getLocale()));


            LOGGER.info("Loading locale file for language {}", config.getLocale());

            final var mapper = new ObjectMapper();
            final var type = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);

            this.dictionary = mapper.readValue(stream, type);


        }


    }

    public String get(@Language("JSONPath") String key, Object... args) {
        return dictionary.get(key).formatted(args);
    }

}
