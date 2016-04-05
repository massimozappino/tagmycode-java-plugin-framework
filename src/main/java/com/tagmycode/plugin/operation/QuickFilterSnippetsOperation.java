package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.QuickSearchDialog;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;


public class QuickFilterSnippetsOperation extends TagMyCodeAsynchronousOperation<Void> {
    private QuickSearchDialog quickSearchDialog;
    private String filterText;

    public QuickFilterSnippetsOperation(QuickSearchDialog quickSearchDialog, String filterText) {
        super(quickSearchDialog);
        this.quickSearchDialog = quickSearchDialog;
        this.filterText = filterText.trim().toLowerCase();
    }

    @Override
    protected Void performOperation() throws Exception {

        quickSearchDialog.populateResults(filterSnippets());
        return null;
    }

    private SnippetCollection filterSnippets() {
        SnippetCollection snippets = quickSearchDialog.getFramework().getData().getSnippets();
        SnippetCollection filteredSnippets = new SnippetCollection();
        for (Snippet snippet : snippets) {
            if (search(filterText, snippet.getCode())
                    || search(filterText, snippet.getTitle())
                    || search(filterText, snippet.getDescription())
                    || search(filterText, snippet.getTags())
                    ) {
                filteredSnippets.add(snippet);
            }
        }
        return filteredSnippets;
    }

    protected boolean search(String query, String fieldValue) {
        fieldValue = fieldValue.toLowerCase();
        String[] tokens = query.split(" ");

        boolean ret = true;
        for (String token : tokens) {
            ret = fieldValue.contains(token) && ret;
        }

        return ret;
    }
}

