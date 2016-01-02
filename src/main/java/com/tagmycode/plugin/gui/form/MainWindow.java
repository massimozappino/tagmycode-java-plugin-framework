package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.IAbstractGUI;
import com.tagmycode.plugin.gui.tab.JClosableTabbedPane;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

public class MainWindow implements IAbstractGUI {
    private final ConsoleTab consoleTab;
    private JPanel mainPanel;

    private JClosableTabbedPane jTabbedPane;

    public MainWindow(final Framework framework) {
        consoleTab = new ConsoleTab();
        jTabbedPane = new JClosableTabbedPane();

        getMainComponent().add(jTabbedPane);
        jTabbedPane.addTab("Snippets", new SnippetsTab(framework).getMainComponent());
        jTabbedPane.addTab("Console", consoleTab.getMainComponent());
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    public ConsoleTab getConsoleTab() {
        return consoleTab;
    }

    public void addSnippetTab(Snippet snippet) {
        final String title = snippet.getTitle();
        jTabbedPane.addClosableTab(title, new SnippetForm(snippet).getMainComponent());
    }
}
