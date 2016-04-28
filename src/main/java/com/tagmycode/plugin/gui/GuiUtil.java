package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Browser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GuiUtil {
    public static void addClickableLink(JLabel label, final String url) {
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new Browser().openUrl(url);
            }
        });

        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void setBold(JLabel jlabel) {
        Font f = jlabel.getFont();
        jlabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
    }
}
