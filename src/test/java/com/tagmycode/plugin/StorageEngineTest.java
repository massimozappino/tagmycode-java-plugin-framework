package com.tagmycode.plugin;

import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.*;
import org.junit.Before;
import org.junit.Test;
import support.FakeStorage;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;


public class StorageEngineTest extends AbstractTest {

    private StorageEngine storageEngine;
    private FakeStorage storageSpy;

    @Before
    public void init() {
        storageSpy = spy(new FakeStorage());
        storageEngine = new StorageEngine(storageSpy);
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
        LanguageCollection languageCollection = resourceGenerate.aLanguageCollection();
        storageEngine.saveLanguageCollection(languageCollection);
        assertEquals(languageCollection, storageEngine.loadLanguageCollection());

        LanguageCollection newLanguageCollection = resourceGenerate.aLanguageCollection();
        newLanguageCollection.add(new DefaultLanguage());
        storageEngine.saveLanguageCollection(newLanguageCollection);
        assertEquals(newLanguageCollection, storageEngine.loadLanguageCollection());
    }

    @Test
    public void testSetPrivateSnippet() throws Exception {
        storageEngine.savePrivateSnippetFlag(true);
        assertTrue(storageEngine.loadPrivateSnippetFlag());

        storageEngine.savePrivateSnippetFlag(false);
        assertFalse(storageEngine.loadPrivateSnippetFlag());
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
    public void testSaveAndLoadLastUpdate() throws IOException {
        Date date = new Date();
        storageEngine.saveLastUpdate(date);
        assertEquals(date, storageEngine.loadLastUpdate());

        Date newDate = new Date();
        storageEngine.saveLastUpdate(newDate);
        assertEquals(newDate, storageEngine.loadLastUpdate());
    }

    @Test
    public void testSaveAndLoadSnippets() throws Exception {
        SnippetCollection snippetCollection = resourceGenerate.aSnippetCollection();
        storageEngine.saveSnippets(snippetCollection);
        assertEquals(snippetCollection, storageEngine.loadSnippets());

        SnippetCollection newSnippetCollection = resourceGenerate.aSnippetCollection();
        newSnippetCollection.add(resourceGenerate.anotherSnippet());
        assertEquals(3, newSnippetCollection.size());

        storageEngine.saveSnippets(newSnippetCollection);
        assertEquals(newSnippetCollection, storageEngine.loadSnippets());
    }

    @Test
    public void clearPreferences() throws Exception {
        storageEngine.saveAccount(resourceGenerate.aUser());
        storageEngine.saveLanguageCollection(resourceGenerate.aLanguageCollection());
        storageEngine.saveLastLanguageUsed(new DefaultLanguage());
        storageEngine.savePrivateSnippetFlag(true);
        storageEngine.saveLastUpdate(new Date());
        storageEngine.saveSnippets(resourceGenerate.aSnippetCollection());
        storageEngine.clearAll();
        assertStorageDataIsCleared(storageEngine);
    }

    protected void assertStorageDataIsCleared(StorageEngine storageEngine) throws IOException, TagMyCodeJsonException, TagMyCodeStorageException {
        assertNull(storageEngine.loadAccount());
        LanguageCollection languageCollection = new LanguageCollection();
        languageCollection.add(new DefaultLanguage());
        assertEquals(languageCollection, storageEngine.loadLanguageCollection());
        assertEquals(new DefaultLanguage(), storageEngine.loadLastLanguageUsed());
        assertNull(storageEngine.loadLastUpdate());
        assertEquals(new SnippetCollection(), storageEngine.loadSnippets());
        assertFalse(storageEngine.loadPrivateSnippetFlag());
    }
}
