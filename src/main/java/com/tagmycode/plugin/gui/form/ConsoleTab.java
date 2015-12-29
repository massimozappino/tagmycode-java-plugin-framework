package com.tagmycode.plugin.gui.form;


import com.tagmycode.plugin.Console;
import com.tagmycode.plugin.gui.AbstractGui;

import javax.swing.*;

import static javax.swing.BorderFactory.createEmptyBorder;

public class ConsoleTab extends AbstractGui {
    private final Console console;
    private JPanel mainPanel;
    private JScrollPane consoleScrollPane;
    private JEditorPane consoleEditorPane;

    public ConsoleTab() {
        consoleScrollPane.setBorder(createEmptyBorder());
        consoleEditorPane.setBorder(createEmptyBorder());
        consoleEditorPane.setEditable(false);
        console = new Console(consoleEditorPane);
        initPopupMenuForJTextComponents(getMainPanel());
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Console getConsole() {
        return console;
    }
}
