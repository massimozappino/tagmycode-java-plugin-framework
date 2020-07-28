package com.tagmycode.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public InputStream getInputStream(String filename) {
        // do not use Thread.currentThread().getContextClassLoader()
        // IntelliJ plugin does not read under the right directory
        return getClass().getClassLoader().getResourceAsStream(filename);
    }

    public Properties getProperties(String filename) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = getInputStream(filename);
        properties.load(inputStream);
        return properties;
    }
}
