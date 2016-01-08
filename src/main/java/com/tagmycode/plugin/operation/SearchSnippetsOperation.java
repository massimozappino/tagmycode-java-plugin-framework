package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SearchSnippetDialog;
import com.tagmycode.sdk.model.SnippetCollection;

public class SearchSnippetsOperation extends TagMyCodeAsynchronousOperation<String> {
    private SearchSnippetDialog searchSnippetDialog;
    private SnippetCollection snippets;
    private String query;

    public SearchSnippetsOperation(SearchSnippetDialog searchSnippetDialog, String query) {
        super(searchSnippetDialog);
        this.searchSnippetDialog = searchSnippetDialog;
        snippets = new SnippetCollection();
        this.query = query;
    }

    @Override
    protected void beforePerformOperation() {
        searchSnippetDialog.getButtonOk().setEnabled(false);
    }

    @Override
    protected String performOperation() throws Exception {
        snippets = searchSnippetDialog.getFramework().getTagMyCode().searchSnippets(query);
        return null;
    }

    @Override
    protected void onComplete() {
        searchSnippetDialog.getButtonOk().setEnabled(true);
    }

    @Override
    protected void onSuccess(String result) {
        searchSnippetDialog.updateListWithSnippets(snippets);
    }
}
