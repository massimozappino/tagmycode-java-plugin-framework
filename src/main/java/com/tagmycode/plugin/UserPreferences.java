package com.tagmycode.plugin;

import java.io.*;
import java.util.Properties;

public class UserPreferences {
    public static final String TOGGLE_FILTER_BUTTON_SELECTED = "toggle_filter_button_selected";
    private Properties properties;
    private File propertyFile;

    public UserPreferences(File propertyFile) {
        properties = new Properties();
        this.propertyFile = propertyFile;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String property = properties.getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(property);
    }

    public void setBoolean(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public void store() throws IOException {
        OutputStream out = new FileOutputStream(propertyFile);
        properties.store(out, this.getClass().toString());
    }

    public void load() {
        try {
            properties.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
