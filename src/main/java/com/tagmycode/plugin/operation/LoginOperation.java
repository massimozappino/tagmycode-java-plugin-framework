package com.tagmycode.plugin.operation;


import com.tagmycode.plugin.ICallback;
import com.tagmycode.plugin.gui.form.AuthorizationDialog;

public class LoginOperation extends TagMyCodeAsynchronousOperation {
    private AuthorizationDialog authorizationDialog;
    private String verificationCode;
    private ICallback[] callbacks;

    public LoginOperation(AuthorizationDialog authorizationDialog, String verificationCode, ICallback[] callbacks) {
        super(authorizationDialog);
        this.authorizationDialog = authorizationDialog;
        this.verificationCode = verificationCode;
        this.callbacks = callbacks;
    }

    @Override
    protected Object performOperation() throws Exception {
        authorizationDialog.getFramework().initialize(verificationCode, callbacks);
        return null;
    }

    @Override
    protected void onComplete() {
        authorizationDialog.getButtonOK().setEnabled(true);
    }

    @Override
    protected void onSuccess(Object result) {
        authorizationDialog.getDialog().dispose();
    }
}
