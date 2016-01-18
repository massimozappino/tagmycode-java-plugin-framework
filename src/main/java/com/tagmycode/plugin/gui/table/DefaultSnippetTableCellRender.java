package com.tagmycode.plugin.gui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DefaultSnippetTableCellRender extends DefaultTableCellRenderer {
    JLabel label;

    public DefaultSnippetTableCellRender() {
        label = new JLabel();
        label.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setBackground(tableCellRendererComponent.getBackground());
        label.setForeground(tableCellRendererComponent.getForeground());

        customText(value);
        return label;
    }

    protected void customText(Object value) {
        label.setText(String.valueOf(value));
    }
}
