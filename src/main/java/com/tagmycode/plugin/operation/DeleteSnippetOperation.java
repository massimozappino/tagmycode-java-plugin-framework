package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.Snippet;

public class DeleteSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    public DeleteSnippetOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
    }

    @Override
    protected Snippet performOperation() throws Exception {
        return null;
    }
}
