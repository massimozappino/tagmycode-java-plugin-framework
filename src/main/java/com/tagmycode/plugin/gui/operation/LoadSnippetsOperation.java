package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.gui.CustomTextSnippetItem;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.SnippetCollection;

public class LoadSnippetsOperation extends TagMyCodeAsynchronousOperation<String> {
    private SnippetsTab snippetsTab;
    private SnippetCollection snippets;

    public LoadSnippetsOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        snippets = null;
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
    protected String performOperation() throws Exception {
        try {
            snippets = snippetsTab.getFramework().getStorage().getSnippets();
        } catch (Throwable e) {
            snippets = new SnippetCollection();
        }
        return null;
    }

    @Override
    protected void onInterrupted() {
        if (snippets == null) {
            snippets = new SnippetCollection();
            updateSnippets(snippets);
        }
    }

    @Override
    protected void onSuccess(String result) {
        updateSnippets(snippets);
    }


}
