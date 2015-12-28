package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;

public class RefreshSnippetOperation extends TagMyCodeAsynchronousOperation<String> {
    private SnippetsTab snippetsTab;
    private ModelCollection<Snippet> snippets;

    public RefreshSnippetOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        snippets = new ModelCollection<Snippet>();
    }

    @Override
    protected void beforePerformOperation() {
        snippetsTab.getRefreshButton().setEnabled(false);
    }

    @Override
    protected String performOperation() throws Exception {
        snippets = snippetsTab.getFramework().getTagMyCode().fetchSnippets();
        return null;
    }

    @Override
    protected void onComplete() {
        snippetsTab.getRefreshButton().setEnabled(true);
    }

    @Override
    protected void onSuccess(String result) {
        snippetsTab.getSnippetsJList().updateWithSnippets(snippets);
    }
}
