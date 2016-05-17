package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractDialog;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends AbstractDialog {
    private JComponent contentPane;
    private JButton buttonCancel;
    private JButton buttonOK;

    public AboutDialog(Framework framework, Frame parent) {
        super(framework, parent);
        buttonOK = new JButton();

        defaultInitWindow();
        initWindow();
    }

    @Override
    protected void initWindow() {
        getDialog().setSize(340, 250);
        getDialog().setResizable(false);
        getDialog().setTitle("About TagMyCode");
        getDialog().setUndecorated(true);
        hideOnFocusLost();
    }

    @Override
    public JButton getButtonOk() {
        return buttonOK;
    }

    @Override
    protected JButton getButtonCancel() {
        return buttonCancel;
    }

    @Override
    public JComponent getMainComponent() {
        return contentPane;
    }

    @Override
    protected void onOK() {

    }

}
