package com.tagmycode.plugin.gui.form;

import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.awt.*;

public class SnippetRenderer extends JLabel implements ListCellRenderer {
    public SnippetRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
        Snippet snippet = (Snippet) object;

        String languageColor = colorToHEx(SystemColor.infoText);

        setText("<html><font color=\"" + languageColor + "\">[" + snippet.getLanguage().getName() + "]</font> <font>" + snippet.getTitle() + "</font></html>");
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }

    private String colorToHEx(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}