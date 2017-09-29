package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.IAbstractGUI;

import javax.swing.*;

public class MainWindow implements IAbstractGUI {
    private final SnippetsPanel snippetsPanel;
    private final LoginPanel loginPanel;
    private JPanel mainPanel;
    private boolean networkingEnabled;

    public MainWindow(final Framework framework) {
        snippetsPanel = new SnippetsPanel(framework);
        loginPanel = new LoginPanel(framework);
    }

    public void setLoggedIn(boolean flag) {
        getMainComponent().removeAll();
        AbstractGui component = flag ? snippetsPanel : loginPanel;
        if (component instanceof SnippetsPanel) {
            ((SnippetsPanel) component).reset();
        }
        getMainComponent().add(component.getMainComponent());
        getMainComponent().revalidate();
        getMainComponent().repaint();
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    public SnippetsPanel getSnippetsPanel() {
        return snippetsPanel;
    }

}
