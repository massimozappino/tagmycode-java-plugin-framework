package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.operation.LoginOperation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LoginDialog extends AbstractDialog {
    private JPanel jPanelVerification;
    private JButton buttonCancel;
    private JPanel contentPane;
    private JButton buttonOK;

    private JTextField verificationCodeTextField;
    private JButton openLinkButton;
    private JTextField authorizationUrl;
    private JTextPane textHelp1;
    private JTextPane textHelp2;
    private Component JPanelVerification;

    public LoginDialog(Framework framework, Frame parent) {
        super(framework, parent);
        defaultInitWindow();
        initWindow();
    }

    @Override
    protected void initWindow() {
        Color background = UIManager.getColor("Panel.background");
        contentPane.setBackground(background);
        textHelp1.setBackground(background);
        textHelp1.setForeground(SystemColor.inactiveCaption);
        textHelp2.setBackground(background);
        textHelp2.setForeground(SystemColor.inactiveCaption);
        authorizationUrl.setBorder(BorderFactory.createEmptyBorder());
        authorizationUrl.setBackground(background);
        authorizationUrl.setForeground(SystemColor.inactiveCaption);
        authorizationUrl.setText(framework.getClient().getAuthorizationUrl());

        getDialog().setSize(380, 350);
        getDialog().setResizable(false);
        getDialog().setTitle("TagMyCode Login");

        authorizationUrl.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                authorizationUrl.selectAll();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

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
        new Browser().openUrl(authorizationUrl.getText());
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
