package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.gui.CustomTextSnippetItem;
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
        SnippetCollection initialSnippetCollection = new SnippetCollection();

        initialSnippetCollection.add(new CustomTextSnippetItem("Loading snippets..."));
        updateSnippets(initialSnippetCollection);
    }

    private void updateSnippets(SnippetCollection snippets) {
        snippetsTab.getAbstractSnippetsListGui().updateWithSnippets(snippets);
    }

    @Override
    protected SnippetCollection performOperation() throws Exception {
        SnippetCollection snippets;
        try {
            snippets = snippetsTab.getFramework().getStorage().getSnippets();
        } catch (Throwable e) {
            snippets = new SnippetCollection();
        }
        return snippets;
    }

    @Override
    protected void onSuccess(SnippetCollection snippets) {
        updateSnippets(snippets);
    }


}
