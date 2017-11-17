package com.tagmycode.plugin.operation;


import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.LoginDialog;
import com.tagmycode.sdk.exception.TagMyCodeException;

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
        Framework framework = loginDialog.getFramework();
        try {
            framework.initialize(verificationCode);
        } catch (TagMyCodeException e) {
            framework.showErrorDialog("Verification code is not valid");
            throw e;
        }
        return null;
    }

    @Override
    protected void onComplete() {
        loginDialog.getButtonOK().setEnabled(true);
    }

    @Override
    protected void onSuccess(Object result) {
        loginDialog.dispose();
    }
}
