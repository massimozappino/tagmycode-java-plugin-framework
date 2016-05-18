package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialog extends AbstractDialog {
    private JComponent contentPane;
    private JButton buttonCancel;
    private JButton licenseButton;
    private JButton reportAnIssueButton;
    private JLabel tagmycodeLinkLabel;
    private JButton buttonOK = new JButton();

    public AboutDialog(Framework framework, Frame parent) {
        super(framework, parent);

        configureButtons();
        GuiUtil.addClickableLink(tagmycodeLinkLabel, "https://tagmycode.com");

        defaultInitWindow();
        initWindow();
    }

    private void configureButtons() {
        licenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Browser().openUrl("http://www.apache.org/licenses/LICENSE-2.0");
            }
        });
        reportAnIssueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Browser().openUrl("https://tagmycode.com/about/contact");
            }
        });
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
