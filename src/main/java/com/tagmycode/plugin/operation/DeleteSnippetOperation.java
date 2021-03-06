package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsPanel;
import com.tagmycode.sdk.model.Snippet;

public class DeleteSnippetOperation extends AbstractSnippetOperation {
    private Snippet snippet;

    public DeleteSnippetOperation(SnippetsPanel snippetsTab, Snippet snippet) {
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
