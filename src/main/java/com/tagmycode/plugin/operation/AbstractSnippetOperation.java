package com.tagmycode.plugin.operation;

import com.j256.ormlite.dao.Dao;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;

import java.sql.SQLException;

public abstract class AbstractSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    protected Framework framework;

    AbstractSnippetOperation(Framework framework, IOnErrorCallback onErrorCallback) {
        super(onErrorCallback);
        this.framework = framework;
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        framework.getPollingProcess().forceScheduleUpdate();
        super.onSuccess(snippet);
    }

    void addSnippet(Snippet snippet) throws SQLException, TagMyCodeException {
        fireDataChanged(getSnippetDao().create(snippet));
    }

    void updateSnippet(Snippet snippet) throws SQLException, TagMyCodeException {
        fireDataChanged(getSnippetDao().update(snippet));
    }

    protected Dao<Snippet, String> getSnippetDao() {
        return framework.getData().getStorageEngine().getDbService().snippetDao();
    }

    protected void fireDataChanged(int rowSaved) throws TagMyCodeException {
        if (rowSaved == 1) {
            framework.getData().loadAll();
            framework.snippetsDataChanged();
        } else {
            throw new TagMyCodeException("Error saving snippet");
        }
    }
}
