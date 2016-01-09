package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.SnippetCollection;

public class RefreshSnippetsOperation extends TagMyCodeAsynchronousOperation<SnippetCollection> {
    private SnippetsTab snippetsTab;

    public RefreshSnippetsOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
    }

    @Override
    protected void beforePerformOperation() {
        snippetsTab.getRefreshButton().setEnabled(false);
    }

    @Override
    protected SnippetCollection performOperation() throws Exception {
        SnippetCollection snippets = snippetsTab.getFramework().getTagMyCode().fetchSnippets();
        snippetsTab.getFramework().getConsole().log(String.format("Fetched %d snippets", snippets.size()));
        return snippets;
    }

    @Override
    protected void onComplete() {
        snippetsTab.getRefreshButton().setEnabled(true);
    }

    @Override
    protected void onSuccess(SnippetCollection snippets) {
        snippetsTab.getSnippetsJTable().updateWithSnippets(snippets);
        try {
            snippetsTab.getFramework().getData().setSnippets(snippets);
        } catch (Exception e) {
            snippetsTab.getFramework().manageTagMyCodeExceptions(new TagMyCodeException(e));
        }
    }
}
