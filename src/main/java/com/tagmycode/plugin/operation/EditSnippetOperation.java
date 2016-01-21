package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;

public class EditSnippetOperation extends AbstractSaveSnippetOperation {

    public EditSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog);
    }

    @Override
    protected Snippet performOperation() throws Exception {
        return snippetDialog.getFramework().getTagMyCode().updateSnippet(getSnippetObject());
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        super.onSuccess(snippet);
        snippetDialog.getFramework().updateSnippet(snippet);
    }

}
