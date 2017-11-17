package com.tagmycode.plugin.gui;

public class WindowType {
    private static WindowType.Type defaultType = WindowType.Type.JDIALOG;

    public static WindowType.Type getDefaultType() {
        return defaultType;
    }

    public static void setDefaultType(WindowType.Type defaultType) {
        WindowType.defaultType = defaultType;
    }

    public enum Type {
        JDIALOG,
        JFRAME
    }

}
