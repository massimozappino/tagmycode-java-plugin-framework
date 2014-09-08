package com.tagmycode.plugin.gui;

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
        setText("<html><font color=\"#600000\">[" + snippet.getLanguage().getName() + "]</font> <font>" + snippet.getTitle() + "</font></html>");
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
