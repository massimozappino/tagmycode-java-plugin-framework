package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.SnippetsDeletions;

public class SyncSnippetsOperation extends TagMyCodeAsynchronousOperation<Void> {
    private Framework framework;
    private SnippetsUpdatePollingProcess process;

    public SyncSnippetsOperation(SnippetsUpdatePollingProcess process) {
        super(process.getFramework());
        this.process = process;
        this.framework = process.getFramework();
    }

    @Override
    protected Void performOperation() throws Exception {
        TagMyCode tagMyCode = framework.getTagMyCode();

        if (tagMyCode.isServiceAvailable()) {
            process.setNetworkAvailable(true);
            Framework.LOGGER.info(String.format("Fetching snippets since: %s", tagMyCode.getLastSnippetsUpdate()));
            SnippetsDeletions emptySnippetsDeletions = new SnippetsDeletions();
            tagMyCode.syncSnippets(framework.getData().getSnippets(), emptySnippetsDeletions);
            Framework.LOGGER.info(String.format("Last snippets update: %s", tagMyCode.getLastSnippetsUpdate()));
        } else {
            process.setNetworkAvailable(false);

            Framework.LOGGER.info("Fetching snippets: Network unreachable");
        }
        return null;
    }

    @Override
    protected void onSuccess(Void ignored) {
        process.syncCompleted();
        framework.snippetsDataChanged();
    }
}
