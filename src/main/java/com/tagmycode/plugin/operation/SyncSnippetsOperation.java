package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.SyncSnippets;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.exception.TagMyCodeApiException;
import com.tagmycode.sdk.exception.TagMyCodeException;
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
    private TagMyCode tagMyCode;

    public SyncSnippetsOperation(SnippetsUpdatePollingProcess syncProcess) {
        super(syncProcess.getFramework());
        this.syncProcess = syncProcess;
        this.framework = syncProcess.getFramework();
        tagMyCode = framework.getTagMyCode();
        this.dbService = framework.getStorageEngine().getDbService();
        this.snippetsStorage = framework.getStorageEngine().getSnippetsStorage();
    }

    @Override
    public Void performOperation() throws Exception {
        if (tagMyCode.isServiceAvailable()) {
            syncProcess.setNetworkAvailable(true);
            Framework.LOGGER.info(String.format("Fetching snippets since: %s", tagMyCode.getLastSnippetsUpdate()));

            try {
                processSync();
            } catch (TagMyCodeApiException apiException) {
                Framework.LOGGER.error(apiException.getMessage());
                framework.resetLastSnippetsUpdate();
                snippetsStorage.deleteNonDirty();
                processSync();
            }

            Framework.LOGGER.info(String.format("Last snippets update: %s", tagMyCode.getLastSnippetsUpdate()));
        } else {
            syncProcess.setNetworkAvailable(false);
            Framework.LOGGER.warn("Fetching snippets: Network unreachable");
        }
        return null;
    }

    protected void processSync() throws SQLException, TagMyCodeException, JSONException, InterruptedException {
        SnippetsDeletions deletedIds = snippetsStorage.findDeletedIds();
        SnippetsCollection dirtyNotDeleted = snippetsStorage.findDirtyNotDeleted();
        deleteLocalDeletions(snippetsStorage.findDeleted());

        allowStopTask();

        SyncSnippets syncSnippets = tagMyCode.syncSnippets(dirtyNotDeleted, deletedIds);

        allowStopTask();

        manageDeletions(syncSnippets.getDeletedSnippets());
        manageChangedSnippets(syncSnippets.getChangedSnippets());

        framework.getData().loadAll();
    }

    private void deleteLocalDeletions(SnippetsCollection deleted) throws SQLException {
        framework.getStorageEngine().getDbService().snippetDao().delete(deleted);
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
        Framework.LOGGER.debug("Changed: " + changedSnippets.size() + " snippets");
    }

    private int createSnippet(Snippet snippet) throws java.sql.SQLException {
        return dbService.snippetDao().create(snippet);
    }

    private int updateSnippet(Snippet snippet) throws java.sql.SQLException {
        return dbService.snippetDao().update(snippet);
    }

    @Override
    protected void onSuccess(Void ignored) {
        framework.snippetsDataChanged();
    }

    @Override
    public void onComplete() {
        syncProcess.syncCompleted();
    }
}
