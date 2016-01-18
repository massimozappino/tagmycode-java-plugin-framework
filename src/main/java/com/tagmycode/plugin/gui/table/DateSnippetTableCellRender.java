package com.tagmycode.plugin.gui.table;

import java.text.DateFormat;
import java.util.Date;

public class DateSnippetTableCellRender extends DefaultSnippetTableCellRender {
    @Override
    protected void customText(Object value) {
        label.setText("");

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        String formattedDate = df.format((Date) value);
        label.setText(formattedDate);
    }
}
