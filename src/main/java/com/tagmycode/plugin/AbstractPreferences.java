package com.tagmycode.plugin;


import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.User;
import org.json.JSONException;

import java.util.Date;

public abstract class AbstractPreferences {
    private final String LANGUAGES = "languages";
    private final String PRIVATE_SNIPPET = "private_snippet";
    private final String LAST_LANGUAGE = "last_language";
    private final String ACCOUNT = "account";
    private final String LAST_UPDATE = "last_update";

    protected abstract String read(String key);

    protected abstract void write(String key, String value);

    protected abstract void unset(String key);

    public User getAccount() throws TagMyCodeJsonException {
        String jsonAccount = read(ACCOUNT);
        if (jsonAccount == null) {
            throw new TagMyCodeJsonException();
        }
        return new User(jsonAccount);
    }

    public void setAccount(User user) throws JSONException {
        write(ACCOUNT, user.toJson());
    }

    public LanguageCollection getLanguageCollection() throws TagMyCodeJsonException {
        String jsonLanguages = read(LANGUAGES);
        if (jsonLanguages == null) {
            throw new TagMyCodeJsonException();
        }
        return new LanguageCollection(jsonLanguages);
    }

    public void setLanguageCollection(LanguageCollection languageCollection) throws JSONException {
        write(LANGUAGES, languageCollection.toJson());
    }

    public boolean getPrivateSnippet() {
        return stringToBoolean(read(PRIVATE_SNIPPET));
    }

    public void setPrivateSnippet(boolean flag) {
        write(PRIVATE_SNIPPET, booleanToString(flag));
    }

    public int getLastLanguageIndex() {
        String value = read(LAST_LANGUAGE);
        int index;
        try {
            index = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            index = 0;
        }
        return index;
    }

    public void setLastLanguageIndex(int lastLanguageId) {
        write(LAST_LANGUAGE, lastLanguageId + "");
    }

    public Date getLastUpdate() {
        long lastTime = Long.valueOf(read(LAST_UPDATE));
        final Date date = new Date();
        date.setTime(lastTime);
        return date;
    }

    public void setLastUpdate(Date date) {
        write(LAST_UPDATE, String.valueOf(date.getTime()));
    }

    public void clearAll() {
        unset(LANGUAGES);
        unset(PRIVATE_SNIPPET);
        unset(LAST_LANGUAGE);
        unset(ACCOUNT);
        unset(LAST_UPDATE);
    }

    protected boolean stringToBoolean(String s) {
        return s.equals("1");
    }

    protected String booleanToString(boolean b) {
        return b ? "1" : "0";
    }

}
