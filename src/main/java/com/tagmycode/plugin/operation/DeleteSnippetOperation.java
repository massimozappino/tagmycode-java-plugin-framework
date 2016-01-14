package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.Snippet;

public class DeleteSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    private SnippetsTab snippetsTab;
    private Snippet snippet;

    public DeleteSnippetOperation(SnippetsTab snippetsTab, Snippet snippet) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        this.snippet = snippet;
    }

    @Override
    protected Snippet performOperation() throws Exception {
        snippetsTab.getFramework().getTagMyCode().deleteSnippet(snippet.getId());
        return snippet;
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        super.onSuccess(snippet);
        snippetsTab.getFramework().deleteSnippet(snippet);
    }
}
