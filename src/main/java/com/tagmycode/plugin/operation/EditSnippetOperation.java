package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;

public class EditSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    private SnippetDialog snippetDialog;

    public EditSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog);
        this.snippetDialog = snippetDialog;
    }

    @Override
    protected void beforePerformOperation() {
        snippetDialog.getButtonOk().setEnabled(false);
    }

    @Override
    protected Snippet performOperation() throws Exception {
        Snippet snippetObject = snippetDialog.createSnippetObject();
        return snippetDialog.getFramework().getTagMyCode().updateSnippet(snippetObject);
    }

    @Override
    protected void onComplete() {
        snippetDialog.getButtonOk().setEnabled(true);
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        snippetDialog.closeDialog();
        snippetDialog.getFramework().updateSnippet(snippet);
    }

}
