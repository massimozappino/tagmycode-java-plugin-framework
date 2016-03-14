package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractGui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends AbstractGui {
    private JButton loginButton;
    private JPanel mainPanel;

    public LoginPanel(final Framework framework) {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.showLoginDialog();
            }
        });
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }
}
