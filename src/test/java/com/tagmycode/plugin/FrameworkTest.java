package com.tagmycode.plugin;


import com.tagmycode.plugin.gui.form.MainWindow;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.authentication.VoidOauthToken;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.User;
import org.junit.Before;
import org.junit.Test;
import support.FakeSecret;
import support.FakeStorage;
import support.ResourceGenerate;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrameworkTest extends AbstractTest {

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
        assertNotNull(framework.getWallet());
        assertNotNull(framework.getMessageManager());
        assertNotNull(framework.getMainWindow());
        assertNotNull(framework.getTaskFactory());
    }

    @Test
    public void testLoadOauthTokenFormWallet() throws Exception {
        framework.getWallet().saveOauthToken(oauthToken);
        assertEquals(oauthToken, framework.loadAccessTokenFormWallet());
    }

    @Test
    public void testRestoreData() throws Exception {
        setValuesForWalletAndData();
        framework.restoreData();
        assertAccessTokenIs(oauthToken);
        assertFrameworkReturnsValidBasicData();
    }

    @Test
    public void restoreDataAfterReloadIde() throws Exception {
        setValuesForWalletAndData();

        User newUser = resourceGenerate.aUser();
        newUser.setUsername("fakeUsername");
        framework.getStorageEngine().saveAccount(newUser);

        LanguageCollection languageCollection = new LanguageCollection();
        languageCollection.add(resourceGenerate.aLanguage());
        framework.getStorageEngine().saveLanguageCollection(languageCollection);

        OauthToken newOauthToken = new OauthToken("newToken", "newToken");
        framework.getWallet().saveOauthToken(newOauthToken);

        FrameworkConfig frameworkConfig = new FrameworkConfig(framework.getWallet().getPasswordKeyChain(), framework.getStorageEngine().getStorage(), framework.getMessageManager(), framework.getTaskFactory(), framework.getParentFrame());
        Framework reloadedFramework = spy(new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new FakeSecret()));

        reloadedFramework.start();

        verify(reloadedFramework, times(1)).saveSnippetsDataChanged();
        assertEquals(newOauthToken, reloadedFramework.getClient().getOauthToken());
        assertEquals("fakeUsername", reloadedFramework.getStorageEngine().loadAccount().getUsername());
        assertEquals(1, reloadedFramework.getLanguageCollection().size());
        assertEquals(resourceGenerate.aLanguage(), reloadedFramework.getLanguageCollection().get(0));
    }

    @Test
    public void start() throws Exception {
        userIsLogged();
        Framework frameworkSpy = spy(framework);

        frameworkSpy.start();

        verify(frameworkSpy, times(1)).saveSnippetsDataChanged();
    }

    @Test
    public void testOnExceptionInRestoreDataThenResetData() throws Exception {
        setValuesForWalletAndData();

        framework.getStorageEngine().clearAll();
        framework.getStorageEngine().saveLanguageCollection(resourceGenerate.aLanguageCollection());

        ((FakeStorage) framework.getStorageEngine().getStorage()).generateExceptionForLanguageCollection();

        framework.restoreData();
        assertAccessTokenIs(oauthToken);
        assertAccountAndLanguageCollectionFromFrameworkAreNull();
    }

    @Test
    public void testFetchAllData() throws Exception {
        userIsLogged();
        framework.fetchBasicData();
        assertFrameworkReturnsValidBasicData();
    }

    @Test
    public void testFetchAndStoreAllData() throws Exception {
        mockClientReturningValidAccountData(framework);
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
        assertDataIsReset(frameworkSpy.getData());
        assertTrue(frameworkSpy.getClient().getOauthToken() instanceof VoidOauthToken);
        assertEquals(null, frameworkSpy.getWallet().loadOauthToken());
        assertStorageDataIsCleared(frameworkSpy.getStorageEngine());
    }

    @Test
    public void testIsInitializedMustBeFalseIfNotAuthenticated() throws Exception {
        assertFalse(framework.isInitialized());
    }

    @Test
    public void testIsInitializedMustBeFalseIfAccountIsNotSet() throws Exception {
        mockClientReturningValidAccountData(framework);
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
        mockClientReturningValidAccountData(framework);
        setAValidAccountAccount();
        setAValidLanguageCollection();
        assertTrue(framework.isInitialized());
    }

    @Test
    public void testUpdateSnippet() throws Exception {
        Framework framework = createSpyFramework();

        framework.updateSnippet(resourceGenerate.aSnippet());

        verify(framework, times(1)).saveSnippetsDataChanged();
    }

    @Test
    public void testSnippetsDataChanged() throws Exception {
        Framework framework = createSpyFramework();
        framework.getTagMyCode().setLastSnippetsUpdate("changed GMT string");

        framework.saveSnippetsDataChanged();

        assertEquals("changed GMT string", framework.getData().getLastSnippetsUpdate());
        verify(framework, times(1)).saveData();
    }


    @Test
    public void testLoadData() throws Exception {
        StorageEngine storage = createStorage();
        storage.saveLastSnippetsUpdate(resourceGenerate.aSnippetsLastUpdate());
        Framework framework = spy(createFramework(storage));

        framework.loadData();

        assertEquals(resourceGenerate.aSnippetsLastUpdate(), framework.getTagMyCode().getLastSnippetsUpdate());
    }

    @Test
    public void testReset() throws Exception {
        framework = spy(framework);
        framework.getTagMyCode().setLastSnippetsUpdate("a custom date");
        framework.getClient().setOauthToken(new OauthToken("aaa", "bbb"));
        framework.getData().setAccount(resourceGenerate.aUser());

        framework.reset();

        verify(framework, times(1)).snippetsDataChanged();
        assertEquals(null, framework.getTagMyCode().getLastSnippetsUpdate());
        assertEquals(new VoidOauthToken(), framework.getClient().getOauthToken());
        assertEquals(null, framework.getData().getAccount());
    }

    @Test
    public void testShowEditSnippetDialog() throws Exception {
        SnippetDialogFactory snippetDialogFactory = mock(SnippetDialogFactory.class);
        when(snippetDialogFactory.create(framework, null)).thenReturn(new SnippetDialog(framework, null));
        framework.setSnippetDialogFactory(snippetDialogFactory);
        framework.showEditSnippetDialog(null);

        verify(snippetDialogFactory, times(0)).create((Framework) any(), (Frame) any());

        framework.showEditSnippetDialog(new ResourceGenerate().aSnippet());
        verify(snippetDialogFactory, times(1)).create((Framework) any(), (Frame) any());
    }

    protected void assertAccessTokenIs(OauthToken accessToken) {
        assertEquals(accessToken, framework.getClient().getOauthToken());
    }

    protected void assertPreferencesReturnsValidData() throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), framework.getData().getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), framework.getLanguageCollection());
    }

    protected void assertAccountAndLanguageCollectionFromFrameworkAreNull() throws TagMyCodeJsonException {
        assertEquals(null, framework.getData().getAccount());
        assertLanguageCollectionIsDefault();
    }

    private void assertLanguageCollectionIsDefault() {
        assertEquals(new DefaultLanguageCollection(), framework.getLanguageCollection());
    }

    protected void assertFrameworkReturnsValidBasicData() throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), framework.getData().getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), framework.getLanguageCollection());
    }

    protected void setValuesForWalletAndData() throws Exception {
        framework.getWallet().saveOauthToken(oauthToken);
        framework.getStorageEngine().saveAccount(resourceGenerate.aUser());
        framework.getStorageEngine().saveLanguageCollection(resourceGenerate.aLanguageCollection());
        framework.getStorageEngine().saveSnippets(resourceGenerate.aSnippetCollection());
    }

    protected void setAValidLanguageCollection() throws IOException, TagMyCodeJsonException {
        framework.setLanguageCollection(resourceGenerate.aLanguageCollection());
    }

    protected void setAValidAccountAccount() throws IOException, TagMyCodeJsonException {
        framework.getData().setAccount(resourceGenerate.aUser());
    }

    private void setAccessToken() throws TagMyCodeException {
        framework.getClient().setOauthToken(oauthToken);
    }

    private void userIsLogged() throws Exception {
        setValuesForWalletAndData();
        mockClientReturningValidAccountData(framework);
    }

}
