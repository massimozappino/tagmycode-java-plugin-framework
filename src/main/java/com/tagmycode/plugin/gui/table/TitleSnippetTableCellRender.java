package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.IconResources;

import javax.swing.*;

public class TitleSnippetTableCellRender extends DefaultSnippetTableCellRender {
    static ImageIcon icon = IconResources.createImageIcon("snippet.png");

    @Override
    protected void customLabel(Object value) {
        label.setText((String) value);
        label.setIcon(icon);
    }
}
