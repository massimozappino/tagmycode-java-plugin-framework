package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.IAbstractGUI;

import javax.swing.*;

public class MainWindow implements IAbstractGUI {
    private final SnippetsTab snippetsTab;
    private final ConsoleTab consoleTab;
    private JPanel mainPanel;

    private JTabbedPane jTabbedPane;

    public MainWindow(final Framework framework) {
        snippetsTab = new SnippetsTab(framework);
        consoleTab = new ConsoleTab();
        jTabbedPane.addTab("Snippets", snippetsTab.getMainComponent());
        jTabbedPane.addTab("Console", consoleTab.getMainComponent());
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    public ConsoleTab getConsoleTab() {
        return consoleTab;
    }

    public SnippetsTab getSnippetsTab() {
        return snippetsTab;
    }
}
