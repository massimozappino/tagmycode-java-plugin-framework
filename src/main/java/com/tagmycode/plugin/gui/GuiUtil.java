package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GuiUtil {
    public static void addClickableLink(final Framework framework, JLabel label, final String url) {
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                framework.openUrlInBrowser(url);
            }
        });

        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void setBold(JLabel jlabel) {
        Font f = jlabel.getFont();
        jlabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
    }

    public static void setPlaceholder(String placeholder, JTextComponent component) {
        new TextPrompt(placeholder, component, TextPrompt.Show.FOCUS_LOST).changeAlpha(0.5f);
    }

    public static void makeTransparentButton(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
    }

    public static void makeTransparentTextField(JTextField jTextField) {
        jTextField.setOpaque(false);
        removeBorder(jTextField);
        jTextField.setBackground(null);
    }

    public static void removeBorder(JComponent component) {
        component.setBorder(new EmptyBorder(component.getInsets()));
    }
}
