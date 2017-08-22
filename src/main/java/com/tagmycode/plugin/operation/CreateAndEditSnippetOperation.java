package com.tagmycode.plugin.operation;

import com.j256.ormlite.dao.Dao;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;

import java.sql.SQLException;

public class CreateAndEditSnippetOperation extends AbstractSaveSnippetOperation {

    private final Framework framework;

    public CreateAndEditSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog);
        this.framework = snippetDialog.getFramework();
    }

    @Override
    protected Snippet performOperation() throws Exception {
        Snippet snippetObject = getSnippetObject();
        if (snippetObject.getLocalId() == 0) {
            addSnippet(snippetObject);
        } else {
            updateSnippet(snippetObject);
        }

        return snippetObject;
    }

    public void addSnippet(Snippet snippet) throws SQLException, TagMyCodeException {
        fireDataChanged(framework.getData().getStorageEngine().getDbService().snippetDao().create(snippet));
    }

    public void updateSnippet(Snippet snippet) throws SQLException, TagMyCodeException {
        fireDataChanged(getSnippetDao().update(snippet));
    }

    private Dao<Snippet, String> getSnippetDao() {
        return framework.getData().getStorageEngine().getDbService().snippetDao();
    }

    private void fireDataChanged(int rowSaved) throws TagMyCodeException {
        if (rowSaved == 1) {
            framework.getData().loadAll();
            framework.snippetsDataChanged();
        } else {
            throw new TagMyCodeException("Error saving snippet");
        }
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        super.onSuccess(snippet);
        framework.getPollingProcess().forceScheduleUpdate();
    }

}
