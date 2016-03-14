package com.tagmycode.plugin.operation;


import com.tagmycode.plugin.ICallback;
import com.tagmycode.plugin.gui.form.LoginDialog;

public class LoginOperation extends TagMyCodeAsynchronousOperation {
    private LoginDialog loginDialog;
    private String verificationCode;
    private ICallback[] callbacks;

    public LoginOperation(LoginDialog loginDialog, String verificationCode, ICallback[] callbacks) {
        super(loginDialog);
        this.loginDialog = loginDialog;
        this.verificationCode = verificationCode;
        this.callbacks = callbacks;
    }

    @Override
    protected Object performOperation() throws Exception {
        loginDialog.getFramework().initialize(verificationCode, callbacks);
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
