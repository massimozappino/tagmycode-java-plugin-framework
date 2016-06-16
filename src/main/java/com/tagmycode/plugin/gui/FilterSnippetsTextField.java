package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FilterSnippetsTextField extends JTextField {

    private Framework framework;
    private SnippetsTable snippetsTable;
    private FilterSnippetsOperation filterSnippetsOperation = null;

    public FilterSnippetsTextField(Framework framework, SnippetsTable snippetsTable) {
        assert framework != null;
        assert snippetsTable != null;
        this.framework = framework;
        this.snippetsTable = snippetsTable;

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doFilter();
            }
        });
    }


    public void doFilter() {
        if (filterSnippetsOperation != null) {
            filterSnippetsOperation.stop();
        }
        filterSnippetsOperation = new FilterSnippetsOperation(framework, snippetsTable, getText());
        filterSnippetsOperation.start();
    }
}
