package com.tagmycode.plugin.gui;

import java.awt.*;

public class CenterLocation {
    private final int x;
    private final int y;

    public CenterLocation(Frame frame, Window window) {
        this(frame, window, CenterLocationType.CENTER_FRAME);
    }

    public CenterLocation(Frame frame, Window window, CenterLocationType centerLocationType) {
        int width;
        int height;
        int offsetX = 0;
        int offsetY = 0;

        if (frame == null || centerLocationType == CenterLocationType.CENTER_SCREEN) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            width = (int) screenSize.getWidth();
            height = (int) screenSize.getHeight();
        } else {
            width = frame.getWidth();
            height = frame.getHeight();
            offsetX = frame.getX();
            offsetY = frame.getY();
        }
        x = offsetX + (width / 2) - (window.getWidth() / 2);
        y = offsetY + (height / 2) - (window.getHeight() / 2);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
