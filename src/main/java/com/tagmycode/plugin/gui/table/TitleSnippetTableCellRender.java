package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.IconResources;

import javax.swing.*;
import java.awt.*;

public class TitleSnippetTableCellRender extends DefaultSnippetTableCellRender {
    static ImageIcon icon = IconResources.createImageIcon("snippet.png");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setText((String) value);
        label.setIcon(icon);

        return label;
    }
}
