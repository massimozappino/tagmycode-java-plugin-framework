package com.tagmycode.plugin.gui;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertEquals;

public class CenterLocationTest {

    @Test
    public void locationWithUpLeftCornerInitialPosition() throws Exception {
        Frame frame = getFrame();
        frame.setLocation(0, 0);

        CenterLocation location = new CenterLocation(frame, getjDialog());

        assertEquals(360, location.getX());
        assertEquals(260, location.getY());
    }

    @Test
    public void locationWithCustomInitialPosition() throws Exception {
        Frame frame = getFrame();
        frame.setLocation(114, 63);

        CenterLocation location = new CenterLocation(frame, getjDialog());

        assertEquals(474, location.getX());
        assertEquals(323, location.getY());
    }

    private JDialog getjDialog() {
        JDialog dialog = new JDialog();
        dialog.setSize(80, 80);
        return dialog;
    }

    private Frame getFrame() {
        Frame frame = new JFrame();
        frame.setSize(800, 600);
        return frame;
    }

}