package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.model.*;

public class Data {

    private User account;
    private LanguageCollection languages;
    private SnippetCollection snippets;
    private StorageEngine storage;

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
    }

    public void saveAll() throws TagMyCodeStorageException {
        storage.saveAccount(getAccount());
        storage.saveLanguageCollection(getLanguages());
        storage.saveSnippets(getSnippets());
    }
}
