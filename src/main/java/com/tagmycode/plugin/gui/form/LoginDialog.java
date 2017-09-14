package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.operation.LoginOperation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.tagmycode.plugin.gui.GuiUtil.addClickableLink;

public class LoginDialog extends AbstractDialog {
    private JPanel jPanelVerification;
    private JButton buttonCancel;
    private JPanel contentPane;
    private JButton buttonOK;

    private JTextField verificationCodeTextField;
    private JButton openLinkButton;
    private JLabel signupLabel;

    public LoginDialog(Framework framework, Frame parent) {
        super(framework, parent);
        defaultInitWindow();
        initWindow();
    }

    @Override
    protected void initWindow() {
        Color background = UIManager.getColor("Panel.background");
        contentPane.setBackground(background);
        addClickableLink(framework, signupLabel, "https://tagmycode.com/account/signup");

        getDialog().setSize(350, 300);
        getDialog().setResizable(false);
        getDialog().setTitle("TagMyCode Login");

        openLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenLink();
            }
        });
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
        buttonOK.setEnabled(false);
        try {
            checkVerificationCodeInput();
            createLoginOperation().runWithTask(framework.getTaskFactory(), "Logging in");
        } catch (TagMyCodeGuiException e) {
            onError(e);
        }
    }

    private LoginOperation createLoginOperation() {
        return new LoginOperation(this, verificationCodeTextField.getText());
    }

    private void onOpenLink() {
        framework.openUrlInBrowser(framework.getTagMyCode().getAuthorizationUrl());
        verificationCodeTextField.requestFocus();
    }

    private void checkVerificationCodeInput() throws TagMyCodeGuiException {
        if (verificationCodeTextField.getText().length() < 4) {
            verificationCodeTextField.requestFocus();
            buttonOK.setEnabled(true);
            throw new TagMyCodeGuiException("Insert a valid verification code");
        }
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

    public JTextField getVerificationCodeTextField() {
        return verificationCodeTextField;
    }
}
