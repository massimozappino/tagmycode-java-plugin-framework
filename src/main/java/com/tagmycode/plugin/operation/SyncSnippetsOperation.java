package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.SyncSnippets;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.exception.TagMyCodeApiException;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetsCollection;
import com.tagmycode.sdk.model.SnippetsDeletions;
import com.tagmycode.sdk.model.SnippetsStorage;
import org.json.JSONException;

import java.sql.SQLException;

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

            SnippetsCollection dirtySnippets = snippetsStorage.findDirtyNotDeleted();
            try {
                SyncSnippets syncSnippets = tagMyCode.syncSnippets(dirtySnippets, snippetsStorage.findDeletedIds());
                manageDeletions(syncSnippets.getDeletedSnippets());
                manageChangedSnippets(syncSnippets.getChangedSnippets());
            } catch (TagMyCodeApiException apiException) {
                Framework.LOGGER.error(apiException.getMessage());
                //TODO  Reload all snippets
                // keep all dirty snippets
            }
            framework.getData().loadAll();

            Framework.LOGGER.info(String.format("Last snippets update: %s", tagMyCode.getLastSnippetsUpdate()));
        } else {
            syncProcess.setNetworkAvailable(false);
            Framework.LOGGER.warn("Fetching snippets: Network unreachable");
        }
        return null;
    }

    private void manageDeletions(SnippetsDeletions snippetsDeletions) throws SQLException, JSONException {
        int snippetsDeleted = 0;
        for (Integer deletion : snippetsDeletions) {
            Snippet foundSnippetToDelete = snippetsStorage.findBySnippetId(deletion);
            if (foundSnippetToDelete != null) {
                dbService.snippetDao().deleteById(String.valueOf(foundSnippetToDelete.getLocalId()));
                snippetsDeleted++;
                Framework.LOGGER.debug("Deleting: " + foundSnippetToDelete.toJson());
            }
        }
        Framework.LOGGER.debug("Deleted: " + snippetsDeleted + " snippets");
    }

    private void manageChangedSnippets(SnippetsCollection changedSnippets) throws SQLException {
        for (Snippet snippet : changedSnippets) {
            Snippet foundSnippet = snippetsStorage.findBySnippetId(snippet.getId());
            if (foundSnippet == null) {
                Snippet newSnippetWithLocalId = snippetsStorage.findByLocalId(snippet.getLocalId());
                if (newSnippetWithLocalId == null) {
                    createSnippet(snippet);
                } else {
                    updateSnippet(snippet);
                }
            } else {
                snippet.setLocalId(foundSnippet.getLocalId());
                updateSnippet(snippet);
            }
        }
        Framework.LOGGER.debug("Changed: " + changedSnippets.size()+ " snippets");

    }

    private int createSnippet(Snippet snippet) throws java.sql.SQLException {
        return dbService.snippetDao().create(snippet);
    }

    private int updateSnippet(Snippet snippet) throws java.sql.SQLException {
        return dbService.snippetDao().update(snippet);
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
