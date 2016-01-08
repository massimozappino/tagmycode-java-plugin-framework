package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.SnippetCollection;

public class LoadSnippetsOperation extends TagMyCodeAsynchronousOperation<SnippetCollection> {
    private SnippetsTab snippetsTab;

    public LoadSnippetsOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
    }

    @Override
    protected void beforePerformOperation() {
        // TODO disable all buttons
    }

    private void updateSnippets(SnippetCollection snippets) {
        snippetsTab.getSnippetsJTable().updateWithSnippets(snippets);
    }

    @Override
    protected SnippetCollection performOperation() throws Exception {
        SnippetCollection snippets;
        try {
            snippets = snippetsTab.getFramework().getData().getSnippets();
        } catch (Throwable e) {
            snippets = new SnippetCollection();
        }
        return snippets;
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        // TODO enable all buttons
    }

    @Override
    protected void onSuccess(SnippetCollection snippets) {
        updateSnippets(snippets);
    }


}
