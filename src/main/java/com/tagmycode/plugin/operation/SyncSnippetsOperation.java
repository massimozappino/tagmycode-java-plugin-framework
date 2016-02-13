package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.SnippetsDeletions;

public class SyncSnippetsOperation extends TagMyCodeAsynchronousOperation<Void> {
    private SnippetsTab snippetsTab;
    private Framework framework;

    public SyncSnippetsOperation(SnippetsTab snippetsTab) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        framework = snippetsTab.getFramework();
    }

    @Override
    protected void beforePerformOperation() {
        snippetsTab.getRefreshButton().setEnabled(false);
    }

    @Override
    protected Void performOperation() throws Exception {
        TagMyCode tagMyCode = framework.getTagMyCode();

        Framework.LOGGER.info(String.format("Fetching snippets since: %s", tagMyCode.getLastSnippetsUpdate()));

        SnippetsDeletions emptySnippetsDeletions = new SnippetsDeletions();
        tagMyCode.syncSnippets(framework.getData().getSnippets(), emptySnippetsDeletions);

        Framework.LOGGER.info(String.format("Last snippets update: %s", tagMyCode.getLastSnippetsUpdate()));
        return null;
    }

    @Override
    protected void onComplete() {
        snippetsTab.getRefreshButton().setEnabled(true);
    }

    @Override
    protected void onSuccess(Void ignored) {
        framework.snippetsDataChanged();
    }
}