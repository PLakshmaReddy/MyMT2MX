package com.example.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final String PROPERTIES_FILE = "mapping.properties";
    private final Properties properties;

    public Configuration() {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IOException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            // For simplicity, we're not handling the exception in a more robust way.
            // In a real application, you might want to log this or throw a custom exception.
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
