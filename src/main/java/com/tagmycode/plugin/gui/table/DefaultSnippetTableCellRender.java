package com.tagmycode.plugin.gui.table;

import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DefaultSnippetTableCellRender extends DefaultTableCellRenderer {
    JLabel label;

    public DefaultSnippetTableCellRender() {
        label = new JLabel();
        label.setOpaque(true);
        Color col;
        col = DefaultLookup.getColor(this, ui, "Table.focusCellForeground");
        if (col != null) {
            label.setForeground(col);
        }
        col = DefaultLookup.getColor(this, ui, "Table.focusCellBackground");
        if (col != null) {
            label.setBackground(col);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        label.setText(value.toString());
        return label;
    }
}
