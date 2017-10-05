package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;

public class EditSnippetOperation extends AbstractSnippetOperation {

    private SnippetDialog snippetDialog;

    public EditSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog.getFramework(), snippetDialog);
        this.snippetDialog = snippetDialog;
    }

    @Override
    protected Snippet performOperation() throws Exception {
        Snippet snippetObject = getSnippetObject();
        if (snippetObject.getLocalId() == 0) {
            addSnippet(snippetObject);
        } else {
            updateSnippet(snippetObject);
        }

        return snippetObject;
    }

    @Override
    protected void beforePerformOperation() {
        snippetDialog.getButtonOk().setEnabled(false);
    }

    @Override
    protected void onComplete() {
        snippetDialog.getButtonOk().setEnabled(true);
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        snippetDialog.snippetMarkedAsSaved();
        snippetDialog.closeDialog();
        super.onSuccess(snippet);
    }

    private Snippet getSnippetObject() {
        return snippetDialog.createSnippetObject();
    }

}
