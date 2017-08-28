package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.Snippet;

public class DeleteSnippetOperation extends AbstractSnippetOperation {
    private Snippet snippet;

    public DeleteSnippetOperation(SnippetsTab snippetsTab, Snippet snippet) {
        super(snippetsTab.getFramework(), snippetsTab);
        this.snippet = snippet;
    }

    @Override
    protected Snippet performOperation() throws Exception {
        snippet.setDeleted(true);
        updateSnippet(snippet);

        return snippet;
    }

}
