package com.tagmycode.plugin;


import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.SnippetCollection;
import com.tagmycode.sdk.model.User;
import org.json.JSONException;

import java.util.Date;

public class Data {
    private static final String SNIPPETS = "snippets";
    private static final String LANGUAGES = "languages";
    private static final String PRIVATE_SNIPPET = "private_snippet";
    private static final String LAST_LANGUAGE = "last_language";
    private static final String ACCOUNT = "account";
    private static final String LAST_UPDATE = "last_update";
    private final IStorage storage;
    private User account;
    private LanguageCollection languageCollection;
    private Boolean privateSnippet;
    private Language lastLanguageUsed;
    private Date lastUpdate;
    private SnippetCollection snippets;

    public Data(IStorage storage) {
        this.storage = storage;
    }

    public User getAccount() throws TagMyCodeJsonException {
        if (account == null) {
            String jsonAccount = read(ACCOUNT);
            if (jsonAccount == null) {
                throw new TagMyCodeJsonException();
            }
            account = new User(jsonAccount);
        }
        return account;
    }

    public void setAccount(User user) throws JSONException {
        write(ACCOUNT, user.toJson());
        account = user;
    }

    public LanguageCollection getLanguageCollection() throws TagMyCodeJsonException {
        if (languageCollection == null) {
            String jsonLanguages = read(LANGUAGES);
            if (jsonLanguages == null) {
                throw new TagMyCodeJsonException();
            }
            languageCollection = new LanguageCollection(jsonLanguages);
        }
        return languageCollection;
    }

    public void setLanguageCollection(LanguageCollection languageCollection) throws JSONException {
        write(LANGUAGES, languageCollection.toJson());
        this.languageCollection = languageCollection;
    }

    public boolean getPrivateSnippet() {
        if (privateSnippet == null) {
            String read = read(PRIVATE_SNIPPET);
            privateSnippet = stringToBoolean(read);
        }
        return privateSnippet;
    }

    public void setPrivateSnippet(boolean flag) {
        write(PRIVATE_SNIPPET, booleanToString(flag));
        privateSnippet = flag;
    }

    public Language getLastLanguageUsed() throws TagMyCodeJsonException {
        if (lastLanguageUsed == null) {
            String jsonLanguage = read(LAST_LANGUAGE);

            if (jsonLanguage == null) {
                throw new TagMyCodeJsonException();
            }
            lastLanguageUsed = new Language(jsonLanguage);
        }
        return lastLanguageUsed;
    }

    public void setLastLanguageUsed(Language lastLanguage) throws JSONException {
        write(LAST_LANGUAGE, lastLanguage.toJson());
        this.lastLanguageUsed = lastLanguage;
    }

    public SnippetCollection getSnippets() throws TagMyCodeJsonException {
        if (snippets == null) {
            String jsonSnippets = read(SNIPPETS);
            if (jsonSnippets == null) {
                throw new TagMyCodeJsonException();
            }

            snippets = new SnippetCollection(jsonSnippets);
        }
        return snippets;
    }

    public void setSnippets(SnippetCollection snippetCollection) throws JSONException {
        write(SNIPPETS, snippetCollection.toJson());
        snippets = snippetCollection;
    }

    public Date getLastUpdate() {
        if (lastUpdate == null) {
            long lastTime = Long.valueOf(read(LAST_UPDATE));
            lastUpdate = new Date();
            lastUpdate.setTime(lastTime);
        }
        return lastUpdate;
    }

    public void setLastUpdate(Date date) {
        write(LAST_UPDATE, String.valueOf(date.getTime()));
        lastUpdate = date;
    }

    public void clearAll() {
        unset(ACCOUNT);
        unset(LANGUAGES);
        unset(PRIVATE_SNIPPET);
        unset(LAST_LANGUAGE);
        unset(LAST_UPDATE);
        unset(SNIPPETS);
        account = null;
        languageCollection = null;
        privateSnippet = null;
        lastLanguageUsed = null;
        lastUpdate = null;
        snippets = null;
    }

    protected void unset(String key) {
        storage.unset(key);
    }

    protected String read(String value) {
        return storage.read(value);
    }

    protected void write(String key, String value) {
        storage.write(key, value);
    }

    protected boolean stringToBoolean(String s) {
        return s != null && s.equals("1");
    }

    protected String booleanToString(boolean b) {
        return b ? "1" : "0";
    }

    protected IStorage getStorage() {
        return storage;
    }

}
