package com.tagmycode.plugin;

import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.*;
import org.junit.Before;
import org.junit.Test;
import support.FakeStorage;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class DataTest extends AbstractTest {

    private Data data;
    private FakeStorage storageSpy;

    @Before
    public void init() {
        storageSpy = spy(new FakeStorage());
        data = new Data(storageSpy);
    }

    @Test
    public void testGetAccount() throws Exception {
        User user = resourceGenerate.anUser();
        when(storageSpy.read(anyString())).thenReturn(user.toJson());
        data.getAccount();
        data.getAccount();
        verify(storageSpy, times(1)).read(anyString());
        assertEquals(user, data.getAccount());
    }

    @Test
    public void testSetAccount() throws Exception {
        User user = resourceGenerate.anUser();
        data.setAccount(user);
        assertEquals(user, data.getAccount());
        verify(storageSpy, times(1)).write(anyString(), eq(user.toJson()));

        data.getAccount();
        verify(storageSpy, times(0)).read(anyString());

        User newUser = resourceGenerate.anUser().setFirstname("John");
        data.setAccount(newUser);
        assertEquals(newUser, data.getAccount());
    }

    @Test
    public void testGetLanguageCollection() throws Exception {
        LanguageCollection languageCollection = resourceGenerate.aLanguageCollection();
        when(storageSpy.read(anyString())).thenReturn(languageCollection.toJson());
        data.getLanguageCollection();
        data.getLanguageCollection();
        verify(storageSpy, times(1)).read(anyString());
        assertEquals(languageCollection, data.getLanguageCollection());
    }

    @Test
    public void testSetLanguageCollection() throws Exception {
        LanguageCollection languageCollection = resourceGenerate.aLanguageCollection();
        data.setLanguageCollection(languageCollection);
        assertEquals(languageCollection, data.getLanguageCollection());
        verify(storageSpy, times(1)).write(anyString(), eq(languageCollection.toJson()));

        data.getLanguageCollection();
        verify(storageSpy, times(0)).read(anyString());

        LanguageCollection newLanguageCollection = resourceGenerate.aLanguageCollection();
        newLanguageCollection.add(new DefaultLanguage());
        data.setLanguageCollection(newLanguageCollection);
        assertEquals(newLanguageCollection, data.getLanguageCollection());
    }

    @Test
    public void testGetPrivateSnippet() {
        when(storageSpy.read(anyString())).thenReturn("1");

        data.getPrivateSnippet();
        data.getPrivateSnippet();
        verify(storageSpy, times(1)).read(anyString());

        assertEquals(true, data.getPrivateSnippet());
    }

    @Test
    public void testSetPrivateSnippet() {
        data.setPrivateSnippet(true);
        assertTrue(data.getPrivateSnippet());
        verify(storageSpy, times(1)).write(anyString(), eq("1"));


        data.getPrivateSnippet();
        verify(storageSpy, times(0)).read(anyString());

        data.setPrivateSnippet(false);
        assertFalse(data.getPrivateSnippet());
    }

    @Test
    public void testGetLastLanguageUsed() throws Exception {
        Language language = resourceGenerate.aLanguage();
        when(storageSpy.read(anyString())).thenReturn(language.toJson());
        data.getLastLanguageUsed();
        data.getLastLanguageUsed();
        verify(storageSpy, times(1)).read(anyString());
        assertEquals(language, data.getLastLanguageUsed());
    }

    @Test
    public void testSetLastLanguageUsed() throws Exception {
        Language language = resourceGenerate.aLanguage();
        data.setLastLanguageUsed(language);
        assertEquals(language, data.getLastLanguageUsed());
        verify(storageSpy, times(1)).write(anyString(), eq(language.toJson()));

        data.getLastLanguageUsed();
        verify(storageSpy, times(0)).read(anyString());

        Language newLanguage = new DefaultLanguage();
        data.setLastLanguageUsed(newLanguage);
        assertEquals(newLanguage, data.getLastLanguageUsed());
    }

    @Test
    public void testGetLastUpdate() {
        Date date = new Date();
        when(storageSpy.read(anyString())).thenReturn(String.valueOf(date.getTime()));

        data.getLastUpdate();
        data.getLastUpdate();
        verify(storageSpy, times(1)).read(anyString());

        assertEquals(date, data.getLastUpdate());
    }

    @Test
    public void testSetLastUpdate() {
        Date date = new Date();
        data.setLastUpdate(date);
        assertEquals(date, data.getLastUpdate());
        verify(storageSpy, times(1)).write(anyString(), eq(String.valueOf(date.getTime())));

        data.getLastUpdate();
        verify(storageSpy, times(0)).read(anyString());

        Date newDate = new Date();
        data.setLastUpdate(newDate);
        assertEquals(newDate, data.getLastUpdate());
    }

    @Test
    public void testGetSnippets() throws Exception {
        SnippetCollection snippetCollection = resourceGenerate.aSnippetCollection();
        when(storageSpy.read(anyString())).thenReturn(snippetCollection.toJson());

        data.getSnippets();
        data.getSnippets();
        verify(storageSpy, times(1)).read(anyString());

        assertEquals(snippetCollection, data.getSnippets());
    }

    @Test
    public void testSetSnippets() throws Exception {
        SnippetCollection snippetCollection = resourceGenerate.aSnippetCollection();
        data.setSnippets(snippetCollection);
        assertEquals(snippetCollection, data.getSnippets());
        verify(storageSpy, times(1)).write(anyString(), eq(snippetCollection.toJson()));

        data.getSnippets();
        verify(storageSpy, times(0)).read(anyString());

        SnippetCollection newSnippetCollection = resourceGenerate.aSnippetCollection();
        newSnippetCollection.add(resourceGenerate.anotherSnippet());
        data.setSnippets(newSnippetCollection);
        assertEquals(newSnippetCollection, data.getSnippets());
    }

    @Test
    public void clearPreferences() throws Exception {
        data.setAccount(resourceGenerate.anUser());
        data.setLanguageCollection(resourceGenerate.aLanguageCollection());
        data.setLastLanguageUsed(new DefaultLanguage());
        data.setPrivateSnippet(true);
        data.setLastUpdate(new Date());
        data.setSnippets(resourceGenerate.aSnippetCollection());
        data.clearAll();
        assertDataAreCleared(data);
    }

    protected void assertDataAreCleared(Data data) throws IOException, TagMyCodeJsonException {
        try {
            data.getAccount();
            fail("Excepted exception");
        } catch (Exception ignored) {
        }

        try {
            data.getLanguageCollection();
            fail("Excepted exception");
        } catch (Exception ignored) {
        }

        try {
            data.getLastLanguageUsed();
            fail("Excepted exception");
        } catch (Exception ignored) {
        }

        try {
            data.getLastUpdate();
            fail("Excepted exception");
        } catch (Exception ignored) {
        }

        try {
            data.getSnippets();
            fail("Excepted exception");
        } catch (Exception ignored) {
        }

        assertFalse(data.getPrivateSnippet());
    }
}
