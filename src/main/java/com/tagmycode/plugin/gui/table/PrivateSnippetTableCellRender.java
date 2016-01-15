package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.IconResources;

import javax.swing.*;
import java.awt.*;

public class PrivateSnippetTableCellRender extends DefaultSnippetTableCellRender {
    static ImageIcon icon = IconResources.createImageIcon("private.png");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setText("");
        if (value.toString().equals("true")) {
            label.setIcon(icon);
        } else {
            label.setIcon(null);
        }

        return label;
    }
}
