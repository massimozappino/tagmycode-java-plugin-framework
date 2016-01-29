package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.model.*;

import java.io.IOException;

public class StorageEngine {
    protected static final String SNIPPETS = "snippets";
    protected static final String LANGUAGES = "languages";
    protected static final String PRIVATE_SNIPPET = "private_snippet";
    protected static final String LAST_LANGUAGE = "last_language";
    protected static final String ACCOUNT = "account";
    private static final String SNIPPETS_LAST_UPDATE = "snippets_last_update";
    private final IStorage storage;

    public StorageEngine(IStorage storage) {
        this.storage = storage;
    }

    public User loadAccount() throws TagMyCodeStorageException {
        try {
            String jsonAccount = read(ACCOUNT);
            return jsonAccount != null ? new User(jsonAccount) : null;
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public void saveAccount(User user) throws TagMyCodeStorageException {
        try {
            write(ACCOUNT, user.toJson());
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public LanguageCollection loadLanguageCollection() {
        LanguageCollection languageCollection;
        try {
            String jsonLanguages = read(LANGUAGES);
            languageCollection = new LanguageCollection(jsonLanguages);
        } catch (Exception e) {
            languageCollection = createDefaultLanguageCollection();
        }

        return languageCollection;
    }

    private LanguageCollection createDefaultLanguageCollection() {
        return new DefaultLanguageCollection();
    }

    public void saveLanguageCollection(LanguageCollection languageCollection) throws TagMyCodeStorageException {
        try {
            String languageCollectionJson = "";
            if (languageCollection != null) {
                languageCollectionJson = languageCollection.toJson();
            }
            write(LANGUAGES, languageCollectionJson);
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }

    }

    public boolean loadPrivateSnippetFlag() {
        String read = null;
        try {
            read = read(PRIVATE_SNIPPET);
        } catch (IOException ignored) {

        }
        return stringToBoolean(read);
    }

    public void savePrivateSnippetFlag(boolean flag) throws TagMyCodeStorageException {
        try {
            write(PRIVATE_SNIPPET, booleanToString(flag));
        } catch (IOException e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public Language loadLastLanguageUsed() {
        Language lastLanguageUsed;
        try {
            lastLanguageUsed = new Language(read(LAST_LANGUAGE));
        } catch (Exception e) {
            lastLanguageUsed = new DefaultLanguage();
        }
        return lastLanguageUsed;
    }

    public void saveLastLanguageUsed(Language lastLanguage) throws TagMyCodeStorageException {
        try {
            if (lastLanguage == null) {
                lastLanguage = createDefaultLanguage();
            }
            write(LAST_LANGUAGE, lastLanguage.toJson());
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    private Language createDefaultLanguage() {
        return new DefaultLanguage();
    }

    public SnippetCollection loadSnippets() {
        SnippetCollection snippets;
        try {
            snippets = new SnippetCollection(read(SNIPPETS));
        } catch (Exception e) {
            snippets = createDefaultSnippetCollection();
        }
        return snippets;
    }

    public void saveSnippets(SnippetCollection snippetCollection) throws TagMyCodeStorageException {
        try {
            if (snippetCollection == null) {
                snippetCollection = createDefaultSnippetCollection();
            }
            write(SNIPPETS, snippetCollection.toJson());
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public void saveLastSnippetsUpdate(String snippetsLastUpdate) throws TagMyCodeStorageException {
        try {
            write(SNIPPETS_LAST_UPDATE, snippetsLastUpdate);
        } catch (IOException e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public String loadLastSnippetsUpdate() throws TagMyCodeStorageException {
        try {
            return read(SNIPPETS_LAST_UPDATE);
        } catch (IOException e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public void clearAll() throws IOException {
        unset(ACCOUNT);
        unset(LANGUAGES);
        unset(PRIVATE_SNIPPET);
        unset(LAST_LANGUAGE);
        unset(SNIPPETS);
    }

    private void unset(String key) throws IOException {
        storage.unset(key);
    }

    private String read(String value) throws IOException {
        return storage.read(value);
    }

    private void write(String key, String value) throws IOException {
        storage.write(key, value);
    }

    private boolean stringToBoolean(String s) {
        return s != null && s.equals("1");
    }

    private String booleanToString(boolean b) {
        return b ? "1" : "0";
    }

    protected IStorage getStorage() {
        return storage;
    }

    private SnippetCollection createDefaultSnippetCollection() {
        return new SnippetCollection();
    }
}
