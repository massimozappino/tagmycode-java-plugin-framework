package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.SnippetsCollection;
import com.tagmycode.sdk.model.User;

public class Data {
    private StorageEngine storage;
    private User account;
    private LanguagesCollection languages;
    private SnippetsCollection snippets;
    private String lastSnippetsUpdate;
    private boolean networkingEnabled;

    public Data(StorageEngine storage) {
        this.storage = storage;
        reset();
    }

    public void clearDataAndStorage() throws TagMyCodeStorageException {
        reset();
        storage.recreateTables();
    }

    protected void reset() {
        account = null;
        languages = new DefaultLanguageCollection();
        snippets = new SnippetsCollection();
        lastSnippetsUpdate = null;
        networkingEnabled = true;
    }

    public User getAccount() {
        return account;
    }

    public void setAccount(User account) {
        this.account = account;
    }

    public LanguagesCollection getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesCollection languages) {
        this.languages = languages;
    }

    public SnippetsCollection getSnippets() {
        return snippets;
    }

    public void setSnippets(SnippetsCollection snippets) {
        this.snippets = snippets;
    }

    public void loadAll() throws TagMyCodeStorageException {
        setAccount(storage.loadAccount());
        setLanguages(storage.loadLanguageCollection());
        String lastSnippetsUpdate = storage.loadLastSnippetsUpdate();
        setLastSnippetsUpdate(lastSnippetsUpdate);
        setSnippets(storage.loadSnippets());
        setNetworkingEnabled(storage.loadNetworkingEnabledFlag());
    }

    public synchronized void saveAll() throws TagMyCodeStorageException {
        storage.saveAccount(getAccount());
        storage.saveLanguageCollection(getLanguages());
        storage.saveSnippets(getSnippets());
        storage.saveLastSnippetsUpdate(getLastSnippetsUpdate());
        storage.saveNetworkingEnabledFlag(isNetworkingEnabled());
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

    public boolean isNetworkingEnabled() {
        return networkingEnabled;
    }

    public void setNetworkingEnabled(boolean networkingEnabled) {
        this.networkingEnabled = networkingEnabled;
    }
}
