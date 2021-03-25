package org.arkngbot.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static org.apache.logging.log4j.core.util.Loader.getClassLoader;

@Component
public class PropertiesSupport {

    private static final Logger LOGGER = LogManager.getLogger(PropertiesSupport.class);

    private Properties properties;

    public PropertiesSupport() {
        properties = new Properties();

        try {
            properties.load(getClassLoader().getResourceAsStream("arkngbot.properties"));
        }
        catch (Exception e) {
            LOGGER.warn("Could not load application properties");
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
