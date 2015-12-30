package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.SnippetCollection;
import org.json.JSONException;

public class RefreshSnippetsOperation extends TagMyCodeAsynchronousOperation<String> {
    private SnippetsTab snippetsTab;
    private SnippetCollection snippets;

    public RefreshSnippetsOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        snippets = new SnippetCollection();
    }

    @Override
    protected void beforePerformOperation() {
        snippetsTab.getRefreshButton().setEnabled(false);
    }

    @Override
    protected String performOperation() throws Exception {
        snippets = snippetsTab.getFramework().getTagMyCode().fetchSnippets();
        snippetsTab.getFramework().getConsole().log(String.format("Fetched %d snippets", snippets.size()));
        return null;
    }

    @Override
    protected void onComplete() {
        snippetsTab.getRefreshButton().setEnabled(true);
    }

    @Override
    protected void onSuccess(String result) {
        snippetsTab.getSnippetsJList().updateWithSnippets(snippets);
        try {
            snippetsTab.getFramework().getStorage().setSnippets(snippets);
        } catch (JSONException e) {
            snippetsTab.getFramework().manageTagMyCodeExceptions(new TagMyCodeException(e));
        }
    }
}
