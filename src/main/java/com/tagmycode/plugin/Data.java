package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.SnippetCollection;
import com.tagmycode.sdk.model.User;

import java.io.IOException;

public class Data {

    private StorageEngine storage;
    private User account;
    private LanguageCollection languages;
    private SnippetCollection snippets;
    private String lastSnippetsUpdate;

    public Data(StorageEngine storage) {
        this.storage = storage;
        reset();
    }

    public void clearDataAndStorage() throws IOException {
        reset();
        storage.clearAll();
    }

    protected void reset() {
        account = null;
        languages = new DefaultLanguageCollection();
        snippets = new SnippetCollection();
    }

    public User getAccount() {
        return account;
    }

    public void setAccount(User account) {
        this.account = account;
    }

    public LanguageCollection getLanguages() {
        return languages;
    }

    public void setLanguages(LanguageCollection languages) {
        this.languages = languages;
    }

    public SnippetCollection getSnippets() {
        return snippets;
    }

    public void setSnippets(SnippetCollection snippets) {
        this.snippets = snippets;
    }

    public void loadAll() throws TagMyCodeStorageException {
        setAccount(storage.loadAccount());
        setLanguages(storage.loadLanguageCollection());
        String lastSnippetsUpdate = storage.loadLastSnippetsUpdate();
        setLastSnippetsUpdate(lastSnippetsUpdate);
        setSnippets(storage.loadSnippets());
    }

    public void saveAll() throws TagMyCodeStorageException {
        storage.saveAccount(getAccount());
        storage.saveLanguageCollection(getLanguages());
        storage.saveSnippets(getSnippets());
        storage.saveLastSnippetsUpdate(getLastSnippetsUpdate());
    }

    public String getLastSnippetsUpdate() {
        return lastSnippetsUpdate;
    }

    public void setLastSnippetsUpdate(String lastSnippetsUpdate) {
        this.lastSnippetsUpdate = lastSnippetsUpdate;
    }

    public StorageEngine getStorageEngine() {
        return storage;
    }
}
