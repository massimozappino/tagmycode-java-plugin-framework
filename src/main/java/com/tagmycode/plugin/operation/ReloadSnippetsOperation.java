package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.SnippetCollection;

public class ReloadSnippetsOperation extends TagMyCodeAsynchronousOperation<SnippetCollection> {
    private SnippetsTab snippetsTab;

    public ReloadSnippetsOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
    }

    @Override
    protected void beforePerformOperation() {
        snippetsTab.getRefreshButton().setEnabled(false);
    }

    @Override
    protected SnippetCollection performOperation() throws Exception {
        return snippetsTab.getFramework().getTagMyCode().fetchSnippets();
    }

    @Override
    protected void onComplete() {
        snippetsTab.getRefreshButton().setEnabled(true);
    }

    @Override
    protected void onSuccess(SnippetCollection snippets) {
        Framework framework = snippetsTab.getFramework();
        framework.updateSnippets(snippets);
        snippetsTab.getSnippetsJTable().updateWithSnippets(snippets);
    }
}
