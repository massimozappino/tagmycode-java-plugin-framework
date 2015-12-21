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
        if (snippet instanceof NoResultsFoundSnippetItem) {
            setText("No results found");
            colorsForNotSelected(list);
        } else {
            setText("<html><font color=\"#600000\">[" + snippet.getLanguage().getName() + "]</font> <font>" + snippet.getTitle() + "</font></html>");
            if (isSelected) {
                colorsForSelected(list.getSelectionBackground(), list.getSelectionForeground());
            } else {
                colorsForNotSelected(list);
            }
        }
        return this;
    }

    private void colorsForSelected(Color selectionBackground, Color selectionForeground) {
        setBackground(selectionBackground);
        setForeground(selectionForeground);
    }

    private void colorsForNotSelected(JList list) {
        colorsForSelected(list.getBackground(), list.getForeground());
    }
}
