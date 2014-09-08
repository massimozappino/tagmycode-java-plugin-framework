package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.gui.SearchSnippetDialog;
import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;

public class SearchSnippetOperation extends TagMyCodeAsynchronousOperation<String> {
    private SearchSnippetDialog searchSnippetDialog;
    private ModelCollection<Snippet> snippets;
    private String query;

    public SearchSnippetOperation(SearchSnippetDialog searchSnippetDialog, String query) {
        super(searchSnippetDialog);
        this.searchSnippetDialog = searchSnippetDialog;
        snippets = new ModelCollection<Snippet>();
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
