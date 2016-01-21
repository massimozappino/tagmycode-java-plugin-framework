package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;

public abstract class AbstractSaveSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    protected SnippetDialog snippetDialog;

    public AbstractSaveSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog);
        this.snippetDialog = snippetDialog;
    }

    @Override
    protected void beforePerformOperation() {
        snippetDialog.getButtonOk().setEnabled(false);
    }

    protected Snippet getSnippetObject() {
        return snippetDialog.createSnippetObject();
    }

    @Override
    protected void onComplete() {
        snippetDialog.getButtonOk().setEnabled(true);
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        snippetDialog.snippetMarkedAsSaved();
        snippetDialog.closeDialog();
    }
}
