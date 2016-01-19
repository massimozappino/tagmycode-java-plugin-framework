package com.tagmycode.plugin.gui.table;

import javax.swing.table.TableCellRenderer;

public class IconTableHeaderCellRender extends KeepSortIconHeaderRenderer {
    public IconTableHeaderCellRender(TableCellRenderer defaultRenderer) {
        super(defaultRenderer);
    }

    @Override
    protected void customLabel(Object value) {
        label.setText("");
    }
}
