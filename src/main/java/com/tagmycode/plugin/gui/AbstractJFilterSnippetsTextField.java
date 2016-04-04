package com.tagmycode.plugin.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class AbstractJFilterSnippetsTextField extends JTextField {
    public AbstractJFilterSnippetsTextField() {

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

    public abstract void doFilter();

}
