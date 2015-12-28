package com.tagmycode.plugin.gui.form;


import com.tagmycode.plugin.Console;
import com.tagmycode.plugin.gui.AbstractForm;

import javax.swing.*;

import static javax.swing.BorderFactory.createEmptyBorder;

public class ConsoleTab extends AbstractForm {

    private final Console console;
    private JPanel mainPanel;
    private JScrollPane consoleScrollPane;
    private JEditorPane consoleEditorPane;

    public ConsoleTab() {
        consoleScrollPane.setBorder(createEmptyBorder());
        consoleEditorPane.setBorder(createEmptyBorder());
        consoleEditorPane.setEditable(false);
        console = new Console(consoleEditorPane);
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Console getConsole() {
        return console;
    }
}
