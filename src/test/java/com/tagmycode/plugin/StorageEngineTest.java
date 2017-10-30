package com.tagmycode.plugin;

import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.*;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import support.AbstractTestBase;
import support.MemDbService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StorageEngineTest extends AbstractTestBase {
    private StorageEngine storageEngine;

    @Before
    public void init() throws SQLException {
        MemDbService dbService = new MemDbService(getClass().getCanonicalName());
        dbService.initialize();
        storageEngine = new StorageEngine(dbService);
    }

    @Test
    public void close() throws Exception {
        DbService dbService = mock(DbService.class);
        StorageEngine storageEngine = new StorageEngine(dbService);
        storageEngine.close();
        verify(dbService, times(1)).close();
    }

    @Test
    public void testSaveAndLoadAccount() throws Exception {
        User user = resourceGenerate.aUser();
        storageEngine.saveAccount(user);
        assertEquals(user, storageEngine.loadAccount());

        User newUser = resourceGenerate.aUser().setFirstname("John");
        storageEngine.saveAccount(newUser);
        assertEquals(newUser, storageEngine.loadAccount());
    }

    @Test
    public void testSaveAndLoadLanguageCollection() throws Exception {
        Language languageToRemove = resourceGenerate.anotherLanguage();
        LanguagesCollection languageCollection = new LanguagesCollection(languageToRemove);
        storageEngine.saveLanguageCollection(languageCollection);
        assertEquals(languageCollection, storageEngine.loadLanguageCollection());

        LanguagesCollection languagesToOverride = new LanguagesCollection();
        languagesToOverride.add(resourceGenerate.aLanguage());
        languagesToOverride.add(new DefaultLanguage());
        storageEngine.saveLanguageCollection(languagesToOverride);
        assertCollectionsAreEquals(languagesToOverride, storageEngine.loadLanguageCollection());
    }

    @Test
    public void testSetPrivateSnippet() throws Exception {
        storageEngine.savePrivateSnippetFlag(true);
        assertTrue(storageEngine.loadPrivateSnippetFlag());

        storageEngine.savePrivateSnippetFlag(false);
        assertFalse(storageEngine.loadPrivateSnippetFlag());
    }

    @Test
    public void testSetNetworkingEnabled() throws Exception {
        assertTrue(storageEngine.loadNetworkingEnabledFlag());

        storageEngine.saveNetworkingEnabledFlag(true);
        assertTrue(storageEngine.loadNetworkingEnabledFlag());

        storageEngine.saveNetworkingEnabledFlag(false);
        assertFalse(storageEngine.loadNetworkingEnabledFlag());
    }

    @Test
    public void testSaveAndLoadLastLanguageUsed() throws Exception {
        Language language = resourceGenerate.aLanguage();
        storageEngine.saveLastLanguageUsed(language);
        assertEquals(language, storageEngine.loadLastLanguageUsed());

        Language newLanguage = new DefaultLanguage();
        storageEngine.saveLastLanguageUsed(newLanguage);
        assertEquals(newLanguage, storageEngine.loadLastLanguageUsed());
    }

    @Test
    public void testSaveAndLoadSnippets() throws Exception {
        storageEngine.saveLanguageCollection(resourceGenerate.aLanguageCollection());

        SnippetsCollection snippetCollection = resourceGenerate.aSnippetCollection();
        storageEngine.saveSnippets(snippetCollection);
        assertCollectionsAreEquals(snippetCollection, storageEngine.loadSnippets());

        SnippetsCollection newSnippetCollection = resourceGenerate.aSnippetCollection();
        newSnippetCollection.add(resourceGenerate.anotherSnippet().setId(3));
        assertEquals(3, newSnippetCollection.size());

        storageEngine.saveSnippets(newSnippetCollection);
        assertCollectionsAreEquals(newSnippetCollection, storageEngine.loadSnippets());
    }

    @Test
    public void testSaveAndLoadLastSnippetsUpdate() throws Exception {
        String lastUpdate = resourceGenerate.aSnippetsLastUpdate();
        storageEngine.saveLastSnippetsUpdate(lastUpdate);
        assertEquals(lastUpdate, storageEngine.loadLastSnippetsUpdate());

        String newLastUpdate = resourceGenerate.aSnippetsLastUpdate();

        storageEngine.saveLastSnippetsUpdate(newLastUpdate);
        assertEquals(newLastUpdate, storageEngine.loadLastSnippetsUpdate());
    }

    @Test
    public void testSaveAndLoadEditorTheme() throws Exception {
        String themeFile = "dark.xml";
        storageEngine.saveEditorTheme(themeFile);
        assertEquals(themeFile, storageEngine.loadEditorTheme());

        String newThemeFile = "newFile.xml";

        storageEngine.saveEditorTheme(newThemeFile);
        assertEquals(newThemeFile, storageEngine.loadEditorTheme());
    }

    @Test
    public void testSaveAndLoadEditorFontSize() throws Exception {
        storageEngine.saveEditorFontSize(20);
        assertEquals(20, storageEngine.loadEditorFontSize());

        storageEngine.saveEditorFontSize(13);
        assertEquals(13, storageEngine.loadEditorFontSize());
    }

    @Test
    public void clearPreferences() throws Exception {
        storageEngine.saveAccount(resourceGenerate.aUser());
        storageEngine.saveLanguageCollection(resourceGenerate.aLanguageCollection());
        storageEngine.saveLastLanguageUsed(new DefaultLanguage());
        storageEngine.savePrivateSnippetFlag(true);
        storageEngine.saveNetworkingEnabledFlag(false);
        storageEngine.saveSnippets(resourceGenerate.aSnippetCollection());
        storageEngine.saveEditorTheme("default.xml");
        storageEngine.saveEditorFontSize(20);
        storageEngine.recreateTables();
        assertStorageDataIsCleared(storageEngine);
    }

    @Test
    public void recreateTables() throws TagMyCodeStorageException, SQLException, IOException, TagMyCodeJsonException {
        storageEngine.saveLanguageCollection(resourceGenerate.aLanguageCollection());
        storageEngine.recreateTables();
        assertEquals(0, storageEngine.getDbService().languageDao().queryForAll().size());
    }

    private void assertStorageDataIsCleared(StorageEngine storageEngine) throws IOException, TagMyCodeJsonException, TagMyCodeStorageException {
        assertNull(storageEngine.loadAccount());
        LanguagesCollection languageCollection = new LanguagesCollection();
        languageCollection.add(new DefaultLanguage());
        assertEquals(languageCollection, storageEngine.loadLanguageCollection());
        assertEquals(new DefaultLanguage(), storageEngine.loadLastLanguageUsed());
        assertEquals(new SnippetsCollection(), storageEngine.loadSnippets());
        assertFalse(storageEngine.loadPrivateSnippetFlag());
        assertTrue(storageEngine.loadNetworkingEnabledFlag());
        assertNull(storageEngine.loadLastSnippetsUpdate());
        assertNull(storageEngine.loadEditorTheme());
        assertEquals(13, storageEngine.loadEditorFontSize());
    }

    private void assertCollectionsAreEquals(List expectedList, List actualList) throws JSONException {
        assertEquals(expectedList.size(), actualList.size());

        boolean found;
        for (Object expectedElement : expectedList) {
            found = false;
            for (Object actualElement : actualList) {
                if (expectedElement.equals(actualElement)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("Actual element not found: " + ((ModelAbstract) expectedElement).toJson());
            }
        }
    }
}
