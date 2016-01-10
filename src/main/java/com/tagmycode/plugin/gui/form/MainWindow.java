package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.IAbstractGUI;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

public class MainWindow implements IAbstractGUI {
    private final SnippetsTab snippetsTab;
    private JPanel mainPanel;
    private Framework framework;

    public MainWindow(final Framework framework) {
        this.framework = framework;

        snippetsTab = new SnippetsTab(framework);
        getMainComponent().add(snippetsTab.getMainComponent());
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    public void addSnippetTab(Snippet snippet) {
        framework.showSnippetDialog(snippet, null);
    }

    public SnippetsTab getSnippetsTab() {
        return snippetsTab;
    }
}
