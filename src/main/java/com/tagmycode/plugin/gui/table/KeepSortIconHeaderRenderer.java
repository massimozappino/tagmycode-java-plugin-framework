package com.tagmycode.plugin.gui.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class KeepSortIconHeaderRenderer implements TableCellRenderer {

    protected JLabel label;
    private TableCellRenderer defaultRenderer;

    public KeepSortIconHeaderRenderer(TableCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (comp instanceof JLabel) {
            label = (JLabel) comp;
            label.setBorder(BorderFactory.createEtchedBorder());
            if (value instanceof Icon) {
                label.setIcon((Icon) value);
            }
            customLabel(value);
        }

        return comp;
    }

    protected void customLabel(Object value) {

    }

}
