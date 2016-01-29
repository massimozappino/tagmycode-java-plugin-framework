package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.SnippetCollection;
import com.tagmycode.sdk.model.User;

public class Data {

    private User account;
    private LanguageCollection languages;
    private SnippetCollection snippets;
    private StorageEngine storage;
    private String lastSnippetsUpdate;

    public Data(StorageEngine storage) {
        this.storage = storage;
    }

    public void reset() {
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
        setSnippets(storage.loadSnippets());
        setLastSnippetsUpdate(storage.loadLastSnippetsUpdate());
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
}
