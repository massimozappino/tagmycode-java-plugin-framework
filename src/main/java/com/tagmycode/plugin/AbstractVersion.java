package com.tagmycode.plugin;

import java.io.IOException;
import java.util.Properties;

public abstract class AbstractVersion {

    private String versionString = "";
    private String buildDate = "";

    public AbstractVersion() {
        try {
            PropertiesReader propertiesReader = new PropertiesReader();
            Properties properties = propertiesReader.getProperties("version.properties");

            versionString = properties.getProperty("version");
            buildDate = properties.getProperty("buildDate");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFrameworkVersion() {
        return versionString;
    }

    public String getFrameworkBuildDate() {
        return buildDate;
    }

    public abstract String getPluginVersion();

    public abstract String getPluginTitle();
}