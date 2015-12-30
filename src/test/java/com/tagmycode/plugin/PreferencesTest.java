package com.tagmycode.plugin;

import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.User;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import support.FakeStorage;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class PreferencesTest extends AbstractTest {

    private FakeStorage preferences;

    @Before
    public void init() {
        preferences = new FakeStorage();
    }

    @Test
    public void setAndGetAccount() throws Exception {
        User user = resourceGenerate.anUser();
        preferences.setAccount(user);
        assertEquals(user, preferences.getAccount());
    }

    @Test
    public void setAndGetLanguageCollection() throws JSONException, TagMyCodeJsonException, IOException {
        LanguageCollection languageCollection = resourceGenerate.aLanguageCollection();
        preferences.setLanguageCollection(languageCollection);
        assertEquals(languageCollection.size(), preferences.getLanguageCollection().size());
    }

    @Test
    public void setAndGetPrivateSnippet() {
        preferences.setPrivateSnippet(true);
        assertEquals(true, preferences.getPrivateSnippet());
    }

    @Test
    public void setAndGetLastLanguageId() {
        int lastLanguageId = 3;
        preferences.setLastLanguageIndex(lastLanguageId);
        assertEquals(lastLanguageId, preferences.getLastLanguageIndex());
    }

    @Test
    public void getLastLanguageIdReturnsZeroIfItsNotSet() {
        assertEquals(0, preferences.getLastLanguageIndex());
    }

    @Test
    public void setAndGetLastUpdate() {
        Date date = new Date();
        preferences.setLastUpdate(date);
        assertEquals(date, preferences.getLastUpdate());
    }

    @Test
    public void clearPreferences() throws Exception {
        preferences.setAccount(resourceGenerate.anUser());
        preferences.setLanguageCollection(resourceGenerate.aLanguageCollection());
        preferences.clearAll();
        assertPreferencesAreCleared(preferences);
    }
}
