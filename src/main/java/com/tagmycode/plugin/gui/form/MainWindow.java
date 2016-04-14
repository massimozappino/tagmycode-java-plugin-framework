package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.IAbstractGUI;

import javax.swing.*;

public class MainWindow implements IAbstractGUI {
    private final SnippetsTab snippetsTab;
    private final LoginPanel loginPanel;
    private JPanel mainPanel;

    public MainWindow(final Framework framework) {
        snippetsTab = new SnippetsTab(framework);
        loginPanel = new LoginPanel(framework);
    }

    public void setLoggedIn(boolean flag) {
        getMainComponent().removeAll();
        AbstractGui component = flag ? snippetsTab : loginPanel;
        if (component instanceof SnippetsTab) {
            ((SnippetsTab) component).reset();
        }
        getMainComponent().add(component.getMainComponent());
        getMainComponent().revalidate();
        getMainComponent().repaint();
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    public SnippetsTab getSnippetsTab() {
        return snippetsTab;
    }
}
