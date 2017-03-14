package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StorageEngine {
    private static final String SNIPPETS = "snippets";
    private static final String LANGUAGES = "languages";
    private static final String PRIVATE_SNIPPET = "private_snippet";
    private static final String NETWORKING_ENABLED = "networking_enabled";
    private static final String LAST_LANGUAGE = "last_language";
    private static final String ACCOUNT = "account";
    private static final String SNIPPETS_LAST_UPDATE = "snippets_last_update";
    private final IStorage storage;
    private final DbService dbService;

    public StorageEngine(IStorage storage, DbService dbService) throws SQLException {
        this.storage = storage;
        this.dbService = dbService;
    }

    public DbService getDbService() {
        return dbService;
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

    public List<Language> loadLanguageCollection() {
        try {
            List<Language> languages = dbService.languageDao().queryForAll();

            if (languages.size() == 0) {
                languages.add(new DefaultLanguage());
            }
            return languages;
        } catch (Exception e) {
            return createDefaultLanguageCollection();
        }
// TODO

//        LanguageCollection languageCollection;
//        try {
//            String jsonLanguages = read(LANGUAGES);
//            languageCollection = new LanguageCollection(jsonLanguages);
//        } catch (Exception e) {
//            languageCollection = createDefaultLanguageCollection();
//        }
//
//        return languageCollection;
    }

    private LanguageCollection createDefaultLanguageCollection() {
        return new DefaultLanguageCollection();
    }

    public void saveLanguageCollection(List<Language> languageCollection) throws TagMyCodeStorageException {
        for (Language language : languageCollection) {
            try {
                //TODO check for null languageList
                dbService.languageDao().createOrUpdate(language);
            } catch (SQLException e) {
                throw new TagMyCodeStorageException(e);
            }
        }
// TODO
//        try {
//            String languageCollectionJson = "";
//            if (languageCollection != null) {
//                languageCollectionJson = languageCollection.toJson();
//            }
//            write(LANGUAGES, languageCollectionJson);
//        } catch (Exception e) {
//            throw new TagMyCodeStorageException(e);
//        }

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


    public boolean loadNetworkingEnabledFlag() {
        String read = "1";
        try {
            read = read(NETWORKING_ENABLED);
        } catch (IOException ignored) {

        }
        if (read == null) read = "1";
        return stringToBoolean(read);
    }

    public void saveNetworkingEnabledFlag(boolean flag) throws TagMyCodeStorageException {
        try {
            write(NETWORKING_ENABLED, booleanToString(flag));
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
        SnippetCollection snippets = createDefaultSnippetCollection();
        try {
            for (Snippet snippet : dbService.snippetDao().queryForAll()) {
                snippets.add(snippet);
            }
        } catch (Exception e) {
            snippets = createDefaultSnippetCollection();
        }
        return snippets;
    }

    public void saveSnippets(List<Snippet> snippetCollection) throws TagMyCodeStorageException {
        try {
            if (snippetCollection == null) {
                snippetCollection = createDefaultSnippetCollection();
            }
            for (Snippet snippet : snippetCollection) {
                dbService.snippetDao().createOrUpdate(snippet);
            }

        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public void saveLastSnippetsUpdate(String snippetsLastUpdate) throws TagMyCodeStorageException {
        try {
            if (snippetsLastUpdate == null) {
                snippetsLastUpdate = "";
            }
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

    public void clearAll() throws IOException, SQLException {
        unset(ACCOUNT);
        unset(LANGUAGES);
        unset(PRIVATE_SNIPPET);
        unset(LAST_LANGUAGE);
        unset(SNIPPETS);
        unset(NETWORKING_ENABLED);
    }

    private void unset(String key) throws IOException, SQLException {
        storage.unset(key);
        dbService.clearAllTables();
    }

    private String read(String value) throws IOException {
        return storage.read(value);
    }

    private void write(String key, String value) throws IOException {
        // Compatibility with other storage engines
        if (value == null) {
            throw new NullPointerException();
        }
        storage.write(key, value);
    }

    private boolean stringToBoolean(String s) {
        return s != null && s.equals("1");
    }

    private String booleanToString(boolean b) {
        return b ? "1" : "0";
    }

    IStorage getStorage() {
        return storage;
    }

    private SnippetCollection createDefaultSnippetCollection() {
        return new SnippetCollection();
    }
}
