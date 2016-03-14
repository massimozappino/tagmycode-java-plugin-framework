package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsForm extends AbstractDialog {
    protected JLabel identity;
    private JPanel mainPanel;
    private JPanel logoutPanel;
    private JButton logoutButton;
    private Framework framework;

    public SettingsForm(final Framework framework, Frame parent) {
        super(framework, parent);
        this.framework = framework;

        defaultInitWindow();
        initWindow();
        fillData();
    }

    private void fillData() {
        identity.setText(framework.getData().getAccount().getEmail());
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    @Override
    protected void initWindow() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.logout();
                closeDialog();
            }
        });
        getDialog().getRootPane().setDefaultButton(null);
        getDialog().setSize(400, 300);
        getDialog().setResizable(true);
    }

    @Override
    protected void onOK() {

    }

    @Override
    public JButton getButtonOk() {
        return new JButton();
    }

    @Override
    protected JButton getButtonCancel() {
        return new JButton();
    }
}
