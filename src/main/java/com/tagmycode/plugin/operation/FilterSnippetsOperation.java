package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

public class FilterSnippetsOperation extends TagMyCodeAsynchronousOperation<SnippetCollection> {
    private SnippetsTab snippetsTab;
    private String filterText;

    public FilterSnippetsOperation(SnippetsTab snippetsTab, String filterText) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        this.filterText = filterText.trim().toLowerCase();
    }

    @Override
    protected SnippetCollection performOperation() throws Exception {
        snippetsTab.getSnippetsTable();
        SnippetCollection filteredSnippets = new SnippetCollection();
        for (Snippet snippet : snippetsTab.getFramework().getData().getSnippets()) {
            if (snippet.getTitle().toLowerCase().contains(filterText)
                    || snippet.getCode().toLowerCase().contains(filterText)
                    || snippet.getDescription().toLowerCase().contains(filterText)
                    || snippet.getTags().toLowerCase().contains(filterText)
                    ) {
                filteredSnippets.add(snippet);
            }
        }

        return filteredSnippets;
    }

    @Override
    protected void onSuccess(SnippetCollection snippets) {
        snippetsTab.getSnippetsTable().fireSnippetsChanged();
    }
}
