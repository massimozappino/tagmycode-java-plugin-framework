package com.tagmycode.plugin;

import java.io.*;
import java.util.Properties;

public class UserPreferences {
    public static final String TOGGLE_FILTER_BUTTON_SELECTED = "toggle_filter_button_selected";
    private Properties properties;
    private File propertyFile;

    public UserPreferences(String fileName) {
        properties = new Properties();
        propertyFile = new File(fileName);
        try {
            load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void add(String key, String value) {
        properties.setProperty(key, value);
        try {
            store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store() throws IOException {
        OutputStream out = new FileOutputStream(propertyFile);
        properties.store(out, this.getClass().toString());
    }

    private void load(FileInputStream fileInputStream) throws IOException {
        properties.load(fileInputStream);
    }

}
