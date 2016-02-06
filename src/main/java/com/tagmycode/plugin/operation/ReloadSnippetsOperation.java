package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.SnippetCollection;

public class ReloadSnippetsOperation extends TagMyCodeAsynchronousOperation<SnippetCollection> {
    private SnippetsTab snippetsTab;
    private Framework framework;
    private String lastSnippetsUpdate;

    public ReloadSnippetsOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        framework = snippetsTab.getFramework();
    }

    @Override
    protected void beforePerformOperation() {
        snippetsTab.getRefreshButton().setEnabled(false);
    }

    @Override
    protected SnippetCollection performOperation() throws Exception {
        Framework.LOGGER.info(String.format("Fetching snippets since: %s", framework.getData().getLastSnippetsUpdate()));

        SnippetCollection snippets = framework.getTagMyCode().fetchSnippetsChanges(framework.getData().getLastSnippetsUpdate());
        lastSnippetsUpdate = framework.getTagMyCode().getLastSnippetUpdate();
        Framework.LOGGER.info(String.format("Fetched %d snippets", snippets.size()));
        Framework.LOGGER.info(String.format("Last snippets update: %s", lastSnippetsUpdate));

        return snippets;
    }

    @Override
    protected void onComplete() {
        snippetsTab.getRefreshButton().setEnabled(true);
    }

    @Override
    protected void onSuccess(SnippetCollection snippets) {
        framework.mergeSnippets(snippets, lastSnippetsUpdate);
    }
}
