package com.tagmycode.plugin.gui;

public class ThemeItem {
    private final String key;
    private final String name;

    public ThemeItem(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return name;
    }
}
