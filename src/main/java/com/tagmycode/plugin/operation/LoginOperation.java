package com.tagmycode.plugin.operation;


import com.tagmycode.plugin.gui.form.LoginDialog;

public class LoginOperation extends TagMyCodeAsynchronousOperation {
    private LoginDialog loginDialog;
    private String verificationCode;

    public LoginOperation(LoginDialog loginDialog, String verificationCode) {
        super(loginDialog);
        this.loginDialog = loginDialog;
        this.verificationCode = verificationCode;
    }

    @Override
    protected Object performOperation() throws Exception {
        loginDialog.getFramework().initialize(verificationCode);
        return null;
    }

    @Override
    protected void onComplete() {
        loginDialog.getButtonOK().setEnabled(true);
    }

    @Override
    protected void onSuccess(Object result) {
        loginDialog.getDialog().dispose();
    }
}
