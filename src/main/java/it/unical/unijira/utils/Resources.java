package it.unical.unijira.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;

public class Resources {

    private final static Logger LOGGER = LoggerFactory.getLogger(Resources.class);

    public static InputStream getResourceAsStream(String resource) {

        LOGGER.info("Loading Resource {}", resource);

        var stream = Resources.class.getResourceAsStream(resource);

        if(stream == null)
            stream = Resources.class.getClassLoader().getResourceAsStream(resource);

        return stream;

    }

}
