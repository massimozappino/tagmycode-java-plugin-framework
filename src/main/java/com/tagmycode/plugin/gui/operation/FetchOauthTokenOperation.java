package com.tagmycode.plugin.gui.operation;


import com.tagmycode.plugin.ICallback;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.gui.AuthorizationDialog;
import com.tagmycode.sdk.exception.TagMyCodeConnectionException;

public class FetchOauthTokenOperation extends TagMyCodeAsynchronousOperation {
    private AuthorizationDialog authorizationDialog;
    private String verificationCode;
    private ICallback[] callbacks;

    public FetchOauthTokenOperation(AuthorizationDialog authorizationDialog, String verificationCode, ICallback[] callbacks) {
        super(authorizationDialog);
        this.authorizationDialog = authorizationDialog;
        this.verificationCode = verificationCode;
        this.callbacks = callbacks;
    }

    @Override
    protected Object performOperation() throws Exception {
        try {
            abstractDialog.getFramework().getClient().fetchOauthToken(verificationCode);
            abstractDialog.getFramework().initialize(callbacks);
        } catch (TagMyCodeConnectionException e) {
            throw new TagMyCodeGuiException("Unable to authenticate");
        }
        return null;
    }

    @Override
    protected void onComplete() {
        authorizationDialog.getButtonOK().setEnabled(true);
    }

    @Override
    protected void onSuccess(Object result) {
        abstractDialog.dispose();
    }
}
