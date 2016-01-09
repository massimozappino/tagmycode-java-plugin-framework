package com.tagmycode.plugin;


import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.authentication.VoidOauthToken;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguage;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.User;
import org.junit.Before;
import org.junit.Test;
import support.FakeSecret;
import support.FakeStorage;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrameworkTest extends AbstractTest {

    private Framework framework;
    private OauthToken oauthToken;

    @Before
    public void init() {
        framework = createFramework();
        oauthToken = new OauthToken("1", "2");
    }

    @Test
    public void testConstructor() {
        assertNotNull(framework.getStorageEngine());
        assertNotNull(framework.getWallet());
        assertNotNull(framework.getMessageManager());
        assertNotNull(framework.getConsole());
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
        assertFrameworkReturnsValidData();
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
        Framework reloadedFramework = new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new FakeSecret());

        assertEquals(newOauthToken, reloadedFramework.getClient().getOauthToken());
        assertEquals("fakeUsername", reloadedFramework.getStorageEngine().loadAccount().getUsername());
        assertEquals(1, reloadedFramework.getLanguageCollection().size());
        assertEquals(resourceGenerate.aLanguage(), reloadedFramework.getLanguageCollection().get(0));
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
        mockClientReturningValidAccountData(framework);
        framework.fetchAllData();
        assertFrameworkReturnsValidData();
    }

    @Test
    public void testFetchAndStoreAllData() throws Exception {
        mockClientReturningValidAccountData(framework);
        framework.fetchAndStoreAllData();
        assertFrameworkReturnsValidData();
        assertPreferencesReturnsValidData();
    }

    @Test
    public void testIsDataToBeRefreshedWithOldDate() throws IOException {
        setDataToBeRefreshed();
        assertTrue(framework.isDataToBeRefreshed());
    }

    @Test
    public void testIsDataNotToBeRefreshedWithFreshDate() throws IOException {
        framework.getStorageEngine().saveLastUpdate(new Date());
        assertFalse(framework.isDataToBeRefreshed());
    }

    @Test
    public void testIsRefreshableIsTrueWithOldData() throws Exception {
        mockClientReturningValidAccountData(framework);
        setDataToBeRefreshed();
        assertTrue(framework.isRefreshable());
    }

    @Test
    public void testIsRefreshableIsFalseWithFreshData() throws Exception {
        mockClientReturningValidAccountData(framework);
        setDataAlreadyRefreshed();
        assertFalse(framework.isRefreshable());
    }

    @Test
    public void testIsNotRefreshableWithoutAuthenticated() throws Exception {
        assertFalse(framework.isRefreshable());
    }

    @Test
    public void testLogout() throws Exception {
        StorageEngine storageEngineMock = mock(StorageEngine.class);
        framework.setStorageEngine(storageEngineMock);
        setValuesForWalletAndData();
        setAccessToken();
        framework.logout();
        assertTrue(framework.getClient().getOauthToken() instanceof VoidOauthToken);
        assertEquals(null, framework.getWallet().loadOauthToken());
        verify(storageEngineMock, times(1)).clearAll();
    }

    @Test
    public void testIsInitializedMustBeFalseIfNotAuthenticated() throws Exception {
        assertFalse(framework.isInitialized());
    }

    @Test
    public void testIsInitializedMustBeFalseIfAccountIsNotSet() throws Exception {
        mockClientReturningValidAccountData(framework);
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
        LanguageCollection languageCollection = new LanguageCollection();
        languageCollection.add(new DefaultLanguage());
        assertEquals(languageCollection, framework.getLanguageCollection());
    }

    protected void assertFrameworkReturnsValidData() throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), framework.getData().getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), framework.getLanguageCollection());
        assertEquals(resourceGenerate.aSnippetCollection(), framework.getData().getSnippets());
    }

    protected void assertDateGreaterOrEqualsThan(Date actual, Date compare) {
        assertTrue(actual.compareTo(compare) >= 0);
    }

    protected void setValuesForWalletAndData() throws Exception {
        framework.getWallet().saveOauthToken(oauthToken);
        framework.getStorageEngine().saveAccount(resourceGenerate.aUser());
        framework.getStorageEngine().saveLanguageCollection(resourceGenerate.aLanguageCollection());
        framework.getStorageEngine().saveSnippets(resourceGenerate.aSnippetCollection());
    }

    protected void setDataToBeRefreshed() throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -2 * Framework.DAYS_BEFORE_RELOAD);
        framework.getStorageEngine().saveLastUpdate(cal.getTime());
    }

    protected void setAValidLanguageCollection() throws IOException, TagMyCodeJsonException {
        framework.setLanguageCollection(resourceGenerate.aLanguageCollection());
    }

    protected void setDataAlreadyRefreshed() throws IOException {
        framework.getStorageEngine().saveLastUpdate(new Date());
    }

    protected void setAValidAccountAccount() throws IOException, TagMyCodeJsonException {
        framework.getData().setAccount(resourceGenerate.aUser());
    }

    private void setAccessToken() throws TagMyCodeException {
        framework.getClient().setOauthToken(oauthToken);
    }

}
