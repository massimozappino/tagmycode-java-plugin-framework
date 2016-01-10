package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.IAbstractGUI;
import com.tagmycode.plugin.gui.tab.JClosableTabbedPane;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

public class MainWindow implements IAbstractGUI {
    private final SnippetsTab snippetsTab;
    private JPanel mainPanel;
    private JClosableTabbedPane jTabbedPane;

    public MainWindow(final Framework framework) {
        jTabbedPane = new JClosableTabbedPane();

        getMainComponent().add(jTabbedPane);
        snippetsTab = new SnippetsTab(framework);
        jTabbedPane.addTab("Snippets", snippetsTab.getMainComponent());

    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    public void addSnippetTab(Snippet snippet) {
        final String title = snippet.getTitle();
        String trimmedTitle = title.substring(0, Math.min(title.length(), 50)).trim();
        if (trimmedTitle.length() != title.length()) {
            trimmedTitle += "...";
        }
        jTabbedPane.addClosableTab(trimmedTitle, new SnippetForm(snippet).getMainComponent());
    }

    public SnippetsTab getSnippetsTab() {
        return snippetsTab;
    }
}
