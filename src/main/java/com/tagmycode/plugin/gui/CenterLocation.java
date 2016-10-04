package com.tagmycode.plugin.gui;

import javax.swing.*;
import java.awt.*;

public class CenterLocation {
    private static CenterLocationType centerType = CenterLocationType.CENTER_FRAME;
    private int x;
    private int y;

    public CenterLocation(Frame frame, JDialog dialog) {
        int width;
        int height;
        int offsetX = 0;
        int offsetY = 0;

        if (frame == null || centerType == CenterLocationType.CENTER_SCREEN) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            width = (int) screenSize.getWidth();
            height = (int) screenSize.getHeight();
        } else {
            width = frame.getWidth();
            height = frame.getHeight();
            offsetX = frame.getX();
            offsetY = frame.getY();
        }
        x = offsetX + (width / 2) - (dialog.getWidth() / 2);
        y = offsetY + (height / 2) - (dialog.getHeight() / 2);
    }

    public static void setCenterType(CenterLocationType centerType) {
        CenterLocation.centerType = centerType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
