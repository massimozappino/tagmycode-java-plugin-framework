package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.ICallback;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.gui.operation.FetchOauthTokenOperation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AuthorizationDialog extends AbstractDialog {
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
    private ICallback[] iCallback;

    public AuthorizationDialog(Framework framework, ICallback[] iCallback, Frame parent) {
        super(framework, parent);
        this.iCallback = iCallback;
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

        setSize(380, 350);
        setResizable(false);
        setTitle("TagMyCode authorization");

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
    protected JPanel getContentPanePanel() {
        return contentPane;
    }

    @Override
    protected void onOK() {
        buttonOK.setEnabled(false);
        try {
            checkVerificationCodeInput();
            // do not use task because of nested tasks
            getFetchOauthTokenOperation().run();
        } catch (TagMyCodeGuiException e) {
            onError(e);
        }
    }

    public FetchOauthTokenOperation getFetchOauthTokenOperation() {
        return new FetchOauthTokenOperation(AuthorizationDialog.this, verificationCodeTextField.getText(), iCallback);
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

    public JButton getOpenLinkButton() {
        return openLinkButton;
    }

    public JTextField getVerificationCodeTextField() {
        return verificationCodeTextField;
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

}
