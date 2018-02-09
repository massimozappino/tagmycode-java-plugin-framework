package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.operation.FilterSnippetsOperation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FilterSnippetsTextField extends JTextField {

    private final FilterSnippetsOperation filterSnippetsOperation;


    public FilterSnippetsTextField(FilterSnippetsOperation filterSnippetsOperation) {
        this.filterSnippetsOperation = filterSnippetsOperation;

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
        filterSnippetsOperation.stop();
        filterSnippetsOperation.setSearchText(getText());
        filterSnippetsOperation.start();
    }
}
