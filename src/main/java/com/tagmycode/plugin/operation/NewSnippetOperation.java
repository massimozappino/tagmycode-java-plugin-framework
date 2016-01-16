package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;

public class NewSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    private SnippetDialog snippetDialog;

    public NewSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog);
        this.snippetDialog = snippetDialog;
    }

    @Override
    protected void beforePerformOperation() {
        snippetDialog.getButtonOk().setEnabled(false);
    }

    @Override
    protected Snippet performOperation() throws Exception {
        return snippetDialog.getFramework().getTagMyCode().createSnippet(snippetDialog.createSnippetObject());
    }

    @Override
    protected void onComplete() {
        snippetDialog.getButtonOk().setEnabled(true);
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        snippetDialog.closeDialog();
        snippetDialog.getFramework().addSnippet(snippet);
    }

}
