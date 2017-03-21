package com.tagmycode.plugin;

import java.io.IOException;
import java.util.Properties;

class DefaultVersion implements IVersion {

    private String versionString = "";
    private String buildDate = "";

    DefaultVersion() {
        try {
            PropertiesReader propertiesReader = new PropertiesReader();
            Properties properties = propertiesReader.getProperties("version.properties");

            versionString = properties.getProperty("version");
            buildDate = properties.getProperty("buildDate");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersionString() {
        return versionString;
    }

    @Override
    public String getBuildDate() {
        return buildDate;
    }
}