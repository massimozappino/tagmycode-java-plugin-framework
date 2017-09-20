package com.tagmycode.plugin;

import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.form.MainWindow;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.VoidOauthToken;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import org.junit.Before;
import org.junit.Test;
import support.AbstractTestBase;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrameworkTest extends AbstractTestBase {

    private Framework framework;
    private OauthToken oauthToken;

    @Before
    public void init() throws Exception {
        framework = createFramework();
        oauthToken = new OauthToken("1", "2");
    }

    @Test
    public void testConstructor() {
        assertNotNull(framework.getData());
        assertNotNull(framework.getMessageManager());
        assertNotNull(framework.getMainWindow());
        assertNotNull(framework.getTaskFactory());
    }

    @Test
    public void testRestoreData() throws Exception {
        setValuesForWalletAndData();
        framework.restoreData();
        assertAccessTokenIs(oauthToken);
        assertFrameworkReturnsValidBasicData();
    }

    @Test
    public void start() throws Exception {
        userIsLogged();
        Framework frameworkSpy = spy(framework);

        frameworkSpy.start();

        verify(frameworkSpy, times(1)).restoreData();
    }

    @Test
    public void testFetchAllData() throws Exception {
        userIsLogged();
        framework.fetchBasicData();
        assertFrameworkReturnsValidBasicData();
    }

    @Test
    public void testFetchAndStoreAllData() throws Exception {
        mockTagMyCodeReturningValidAccountData(framework);
        framework.fetchAndStoreAllData();
        assertFrameworkReturnsValidBasicData();
        assertPreferencesReturnsValidData();
    }

    @Test
    public void testLogout() throws Exception {
        Framework frameworkSpy = createSpyFramework();
        MainWindow mainWindowMock = mock(MainWindow.class);
        when(frameworkSpy.getMainWindow()).thenReturn(mainWindowMock);
        when(mainWindowMock.getSnippetsTab()).thenReturn(mock(SnippetsTab.class));
        setValuesForWalletAndData();
        setAccessToken();

        frameworkSpy.logout();

        verify(mainWindowMock, times(1)).setLoggedIn(false);
        assertDataIsCleared(frameworkSpy.getData());
        assertTrue(frameworkSpy.getTagMyCode().getClient().getOauthToken() instanceof VoidOauthToken);
        assertEquals(null, frameworkSpy.getTagMyCode().loadOauthToken());
        assertDataIsCleared(frameworkSpy.getData());
    }

    @Test
    public void testIsInitializedMustBeFalseIfNotAuthenticated() throws Exception {
        assertFalse(framework.isInitialized());
    }

    @Test
    public void testIsInitializedMustBeFalseIfAccountIsNotSet() throws Exception {
        mockTagMyCodeReturningValidAccountData(framework);
        framework.getData().setAccount(null);
        setAValidLanguageCollection();
        assertFalse(framework.isInitialized());
    }

    @Test
    public void testIsInitializedMustBeFalseIfLanguageCollectionIsNotSet() throws Exception {
        setAValidAccountAccount();
        setAValidLanguageCollection();
        assertFalse(framework.isInitialized());
    }

    @Test
    public void testIsInitializedMustBeTrueWithAllData() throws Exception {
        mockTagMyCodeReturningValidAccountData(framework);
        setAValidAccountAccount();
        setAValidLanguageCollection();
        assertTrue(framework.isInitialized());
    }

    @Test
    public void testSnippetsDataChanged() throws Exception {
        Framework framework = createSpyFramework();
        framework.getTagMyCode().setLastSnippetsUpdate("changed GMT string");

        framework.saveAndTellThatSnippetsDataAreChanged();

        assertEquals("changed GMT string", framework.getData().getLastSnippetsUpdate());
        verify(framework, times(1)).saveData();
    }


    @Test
    public void testLoadData() throws Exception {
        StorageEngine storage = createStorageEngineWithData();
        storage.saveLastSnippetsUpdate(resourceGenerate.aSnippetsLastUpdate());
        Framework framework = spy(createFramework(storage));

        framework.loadData();

        assertEquals(resourceGenerate.aSnippetsLastUpdate(), framework.getTagMyCode().getLastSnippetsUpdate());
    }

    @Test
    public void testReset() throws Exception {
        framework = spy(framework);
        framework.getTagMyCode().setLastSnippetsUpdate("a custom date");
        framework.getTagMyCode().getClient().setOauthToken(new OauthToken("aaa", "bbb"));
        framework.getData().setAccount(resourceGenerate.aUser());

        framework.reset();

        verify(framework, times(1)).snippetsDataChanged();
        assertEquals(null, framework.getTagMyCode().getLastSnippetsUpdate());
        assertEquals(new VoidOauthToken(), framework.getTagMyCode().getClient().getOauthToken());
        assertEquals(null, framework.getData().getAccount());
        assertAccountAndLanguageCollectionFromFrameworkAreNull();
    }

    protected void assertAccessTokenIs(OauthToken accessToken) throws TagMyCodeException {
        assertEquals(accessToken, framework.getTagMyCode().loadOauthToken());
    }

    protected void assertPreferencesReturnsValidData() throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), framework.getData().getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), framework.getData().getLanguages());
    }

    protected void assertAccountAndLanguageCollectionFromFrameworkAreNull() throws TagMyCodeJsonException {
        assertEquals(null, framework.getData().getAccount());
        assertLanguageCollectionIsDefault();
    }

    private void assertLanguageCollectionIsDefault() {
        assertEquals(new DefaultLanguageCollection(), framework.getData().getLanguages());
    }

    protected void assertFrameworkReturnsValidBasicData() throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), framework.getData().getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), framework.getData().getLanguages());
    }

    protected void setValuesForWalletAndData() throws Exception {
        framework.getTagMyCode().getClient().setOauthToken(oauthToken);
        framework.getStorageEngine().saveAccount(resourceGenerate.aUser());
        framework.getStorageEngine().saveLanguageCollection(resourceGenerate.aLanguageCollection());
        framework.getStorageEngine().saveSnippets(resourceGenerate.aSnippetCollection());
    }

    protected void setAValidLanguageCollection() throws IOException, TagMyCodeJsonException, TagMyCodeStorageException {
        framework.setLanguageCollection(resourceGenerate.aLanguageCollection());
    }

    protected void setAValidAccountAccount() throws IOException, TagMyCodeJsonException {
        framework.getData().setAccount(resourceGenerate.aUser());
    }

    private void setAccessToken() throws TagMyCodeException {
        framework.getTagMyCode().getClient().setOauthToken(oauthToken);
    }

    private void userIsLogged() throws Exception {
        setValuesForWalletAndData();
        mockTagMyCodeReturningValidAccountData(framework);
    }

}
