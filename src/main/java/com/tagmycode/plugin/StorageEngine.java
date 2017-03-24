package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StorageEngine {
    private static final String PRIVATE_SNIPPET = "private_snippet";
    private static final String NETWORKING_ENABLED = "networking_enabled";
    private static final String LAST_LANGUAGE = "last_language";
    private static final String ACCOUNT = "account";
    private static final String SNIPPETS_LAST_UPDATE = "snippets_last_update";
    private final DbService dbService;

    public StorageEngine(DbService dbService) throws SQLException {
        this.dbService = dbService;
    }

    public DbService getDbService() {
        return dbService;
    }

    public User loadAccount() throws TagMyCodeStorageException {
        try {
            String jsonAccount = readProperty(ACCOUNT).getValue();
            return jsonAccount != null ? new User(jsonAccount) : null;
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public void saveAccount(User user) throws TagMyCodeStorageException {
        try {
            writeProperty(ACCOUNT, user.toJson());
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public List<Language> loadLanguageCollection() {
        try {
            List<Language> languages = dbService.languageDao().queryForAll();

            if (languages.size() == 0) {
                throw new Exception("Language size is 0");
            }
            return languages;
        } catch (Exception e) {
            return new DefaultLanguageCollection();
        }
    }

    public void saveLanguageCollection(List<Language> languageCollection) throws TagMyCodeStorageException {
        if (languageCollection == null) {
            languageCollection = new LanguageCollection();
        }
        for (Language language : languageCollection) {
            try {
                dbService.languageDao().createOrUpdate(language);
            } catch (SQLException e) {
                throw new TagMyCodeStorageException(e);
            }
        }
    }

    public boolean loadPrivateSnippetFlag() {
        String read = null;
        try {
            read = readProperty(PRIVATE_SNIPPET).getValue();
        } catch (SQLException ignored) {
        }
        return stringToBoolean(read);
    }

    public void savePrivateSnippetFlag(boolean flag) throws TagMyCodeStorageException {
        try {
            writeProperty(PRIVATE_SNIPPET, booleanToString(flag));
        } catch (SQLException e) {
            throw new TagMyCodeStorageException(e);
        }
    }


    public boolean loadNetworkingEnabledFlag() {
        String read = "1";
        try {
            read = readProperty(NETWORKING_ENABLED).getValue();
        } catch (SQLException ignored) {

        }
        if (read == null) read = "1";
        return stringToBoolean(read);
    }

    public void saveNetworkingEnabledFlag(boolean flag) throws TagMyCodeStorageException {
        try {
            writeProperty(NETWORKING_ENABLED, booleanToString(flag));
        } catch (SQLException e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public Language loadLastLanguageUsed() {
        Language lastLanguageUsed;
        try {
            String value = readProperty(LAST_LANGUAGE).getValue();
            if (value == null) {
                throw new Exception("Property value is null");
            }
            lastLanguageUsed = new Language(value);
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
            writeProperty(LAST_LANGUAGE, lastLanguage.toJson());
        } catch (Exception e) {
            throw new TagMyCodeStorageException(e);
        }
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
            writeProperty(SNIPPETS_LAST_UPDATE, snippetsLastUpdate);
        } catch (SQLException e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public String loadLastSnippetsUpdate() throws TagMyCodeStorageException {
        try {
            return readProperty(SNIPPETS_LAST_UPDATE).getValue();
        } catch (SQLException e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    public void clearAll() throws TagMyCodeStorageException {
        try {
            dbService.clearAllTables();
        } catch (SQLException e) {
            throw new TagMyCodeStorageException(e);
        }
    }

    private Property readProperty(String key) throws SQLException {
        Property property = dbService.propertyDao().queryForId(key);
        if (property == null) {
            property = new Property();
        }
        return property;
    }

    private void writeProperty(String key, String value) throws SQLException {
        dbService.propertyDao().createOrUpdate(new Property(key, value));
    }

    private boolean stringToBoolean(String s) {
        return s != null && s.equals("1");
    }

    private String booleanToString(boolean b) {
        return b ? "1" : "0";
    }

    private SnippetCollection createDefaultSnippetCollection() {
        return new SnippetCollection();
    }

    private Language createDefaultLanguage() {
        return new DefaultLanguage();
    }

    public void close() throws TagMyCodeStorageException {
        try {
            dbService.close();
        } catch (IOException e) {
            throw new TagMyCodeStorageException(e);
        }
    }
}
