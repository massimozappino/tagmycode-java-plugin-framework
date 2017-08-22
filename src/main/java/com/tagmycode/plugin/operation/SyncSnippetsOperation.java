package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.SyncSnippets;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetsCollection;
import com.tagmycode.sdk.model.SnippetsDeletions;
import com.tagmycode.sdk.model.SnippetsStorage;

public class SyncSnippetsOperation extends TagMyCodeAsynchronousOperation<Void> {
    private Framework framework;
    private SnippetsUpdatePollingProcess syncProcess;
    private SnippetsStorage snippetsStorage;
    private DbService dbService;

    public SyncSnippetsOperation(SnippetsUpdatePollingProcess syncProcess) {
        super(syncProcess.getFramework());
        this.syncProcess = syncProcess;
        this.framework = syncProcess.getFramework();
        this.dbService = framework.getStorageEngine().getDbService();
        this.snippetsStorage = framework.getStorageEngine().getSnippetsStorage();
    }

    @Override
    protected Void performOperation() throws Exception {
        TagMyCode tagMyCode = framework.getTagMyCode();
        if (tagMyCode.isServiceAvailable()) {
            syncProcess.setNetworkAvailable(true);
            Framework.LOGGER.info(String.format("Fetching snippets since: %s", tagMyCode.getLastSnippetsUpdate()));
            SnippetsDeletions snippetsDeletions = new SnippetsDeletions();

            SnippetsCollection dirtySnippets = snippetsStorage.findDirty();

            SyncSnippets syncSnippets = tagMyCode.syncSnippets(dirtySnippets, snippetsDeletions);

            for (Integer deletion : syncSnippets.getDeletedSnippets()) {

            }

            SnippetsCollection changedSnippets = syncSnippets.getChangedSnippets();
            for (Snippet snippet : changedSnippets) {
                Snippet foundSnippet = snippetsStorage.findBySnippetId(snippet.getId());
                if (foundSnippet == null) {
                    Snippet newSnippetWithLocalId = dbService.snippetDao().queryForId(String.valueOf(snippet.getLocalId()));
                    if (newSnippetWithLocalId == null) {
                        dbService.snippetDao().create(snippet);
                    } else {
                        dbService.snippetDao().update(snippet);
                    }
                } else {
                    snippet.setLocalId(foundSnippet.getLocalId());
                    dbService.snippetDao().update(snippet);
                }
            }
            framework.getData().loadAll();

            Framework.LOGGER.info(String.format("Last snippets update: %s", tagMyCode.getLastSnippetsUpdate()));
        } else {
            syncProcess.setNetworkAvailable(false);
            Framework.LOGGER.info("Fetching snippets: Network unreachable");
        }
        return null;
    }

    @Override
    protected void onSuccess(Void ignored) {
        framework.saveSnippetsDataChanged();
    }

    @Override
    protected void onComplete() {
        syncProcess.syncCompleted();
    }
}
