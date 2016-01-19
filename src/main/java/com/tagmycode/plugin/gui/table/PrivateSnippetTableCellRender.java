package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.IconResources;

import javax.swing.*;

public class PrivateSnippetTableCellRender extends DefaultSnippetTableCellRender {
    static ImageIcon icon = IconResources.createImageIcon("private.png");

    @Override
    protected void customLabel(Object value) {
        label.setText("");
        if (value.toString().equals("true")) {
            label.setIcon(icon);
            label.setHorizontalAlignment(CENTER);
        } else {
            label.setIcon(null);
        }
    }
}
