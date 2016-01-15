package com.tagmycode.plugin.gui.table;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Date;

public class DateSnippetTableCellRender extends DefaultSnippetTableCellRender {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setText("");

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        String formattedDate = df.format((Date) value);
        label.setText(formattedDate);
        return label;
    }
}
