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

        removeBold();

        customLabel(value);
        return label;
    }

    private void removeBold() {
        Font f = label.getFont();
        label.setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
    }

    protected void customLabel(Object value) {
        label.setText(String.valueOf(value));
    }
}
