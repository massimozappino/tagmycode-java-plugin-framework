package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.Snippet;

public class DeleteSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    private final Framework framework;
    private Snippet snippet;

    public DeleteSnippetOperation(SnippetsTab snippetsTab, Snippet snippet) {
        super(snippetsTab);
        this.framework = snippetsTab.getFramework();
        this.snippet = snippet;
    }

    @Override
    protected Snippet performOperation() throws Exception {
        // TODO
        // set deleted true
        // fireDataChanged
        framework.getTagMyCode().deleteSnippet(snippet.getId());
        return snippet;
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        super.onSuccess(snippet);
        framework.getPollingProcess().forceScheduleUpdate();
    }
}
