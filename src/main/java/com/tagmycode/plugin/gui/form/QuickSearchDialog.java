package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.QuickFilterSnippetsTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class QuickSearchDialog extends AbstractDialog {
    private final JButton buttonOk;
    private final JButton buttonCancel;
    private JPanel mainPanel;
    private QuickFilterSnippetsTextField quickFilterSnippetsTextField;

    public QuickSearchDialog(Framework framework, Frame parent) {
        super(framework, parent);
        buttonOk = new JButton();
        buttonCancel = new JButton();
        defaultInitWindow();
        initWindow();
    }

    @Override
    protected void initWindow() {
        getDialog().getRootPane().setDefaultButton(null);
        getDialog().setSize(400, 300);
        getDialog().setResizable(true);
        getDialog().setUndecorated(true);
        getDialog().setModal(false);
        getDialog().addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) {
                hideDialog();
            }

            public void windowGainedFocus(WindowEvent e) {
            }
        });
    }

    @Override
    protected void onOK() {

    }

    @Override
    protected void onCancel() {
        hideDialog();
    }

    @Override
    public JButton getButtonOk() {
        return buttonOk;
    }

    @Override
    protected JButton getButtonCancel() {
        return buttonCancel;
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

}