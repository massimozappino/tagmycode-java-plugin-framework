package com.tagmycode.plugin;


import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.authentication.VoidOauthToken;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
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
        assertNotNull(framework.getStorage());
        assertNotNull(framework.getWallet());
        assertNotNull(framework.getMessageManager());
        assertNotNull(framework.getConsole());
        assertNotNull(framework.getMainWindow());
        assertNotNull(framework.getTaskFactory());
    }

    @Test
    public void testResetData() throws Exception {
        setAValidAccountAccount();
        setAValidLanguageCollection();
        framework.resetData();
        assertAccountAndLanguageCollectionFromFrameworkAreNull();
    }

    @Test
    public void testLoadOauthTokenFormWallet() throws Exception {
        framework.getWallet().saveOauthToken(oauthToken);
        assertEquals(oauthToken, framework.loadAccessTokenFormWallet());
    }

    @Test
    public void testLoadPreferences() throws Exception {
        framework.getStorage().setAccount(resourceGenerate.anUser());
        framework.getStorage().setLanguageCollection(resourceGenerate.aLanguageCollection());
        framework.loadPreferences();
        assertFrameworkReturnsValidAccountAndLanguageCollection();
    }

    @Test
    public void testRestoreData() throws Exception {
        setDataForWalletAndPreferences();
        framework.restoreData();
        assertAccessTokenIs(oauthToken);
        assertFrameworkReturnsValidAccountAndLanguageCollection();
    }

    @Test
    public void restoreDataAfterReloadIde() throws Exception {
        setDataForWalletAndPreferences();

        User newUser = resourceGenerate.anUser();
        newUser.setUsername("fakeUsername");
        framework.getStorage().setAccount(newUser);

        LanguageCollection languageCollection = new LanguageCollection();
        languageCollection.add(resourceGenerate.aLanguage());
        framework.getStorage().setLanguageCollection(languageCollection);

        OauthToken newOauthToken = new OauthToken("newToken", "newToken");
        framework.getWallet().saveOauthToken(newOauthToken);

        FrameworkConfig frameworkConfig = new FrameworkConfig(framework.getWallet().getPasswordKeyChain(), framework.getStorage(), framework.getMessageManager(), framework.getTaskFactory(), framework.getParentFrame());
        Framework reloadedFramework = new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new FakeSecret());

        assertEquals(newOauthToken, reloadedFramework.getClient().getOauthToken());
        assertEquals("fakeUsername", reloadedFramework.getAccount().getUsername());
        assertEquals(1, reloadedFramework.getLanguageCollection().size());
        assertEquals(resourceGenerate.aLanguage(), reloadedFramework.getLanguageCollection().get(0));
    }

    @Test
    public void testOnExceptionInRestoreDataThenResetData() throws Exception {
        setDataForWalletAndPreferences();

        ((FakeStorage) framework.getStorage()).generateExceptionForLanguageCollection();

        framework.restoreData();
        assertAccessTokenIs(oauthToken);
        assertAccountAndLanguageCollectionFromFrameworkAreNull();
    }


    @Test
    public void testStoreData() throws Exception {
        mockClientReturningValidAccountData(framework);
        Date beforeDate = new Date();

        framework.fetchAllData();
        framework.storeData();
        assertPreferencesReturnsValidData();
        assertDateGreaterOrEqualsThan(framework.getStorage().getLastUpdate(), beforeDate);
    }

    @Test
    public void testFetchAllData() throws Exception {
        mockClientReturningValidAccountData(framework);
        framework.fetchAllData();
        assertFrameworkReturnsValidAccountAndLanguageCollection();
    }

    @Test
    public void testFetchAndStoreAllData() throws Exception {
        mockClientReturningValidAccountData(framework);
        framework.fetchAndStoreAllData();
        assertFrameworkReturnsValidAccountAndLanguageCollection();
        assertPreferencesReturnsValidData();
    }

    @Test
    public void testIsDataToBeRefreshedWithOldDate() {
        setDataToBeRefreshed();
        assertTrue(framework.isDataToBeRefreshed());
    }

    @Test
    public void testIsDataNotToBeRefreshedWithFreshDate() {
        framework.getStorage().setLastUpdate(new Date());
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
    public void testRefreshDataIfItIsOld() throws Exception {
        mockClientReturningValidAccountData(framework);
        setDataToBeRefreshed();
        framework.refreshDataIfItIsOld();

        assertConsoleMessageContains(framework.getConsole(), "Data refreshed");
        assertTrue(framework.getClient().isAuthenticated());
        assertFalse(framework.isDataToBeRefreshed());
        assertFrameworkReturnsValidAccountAndLanguageCollection();
    }

    @Test
    public void testLogout() throws Exception {
        setDataForWalletAndPreferences();
        setAccessToken();
        framework.logout();
        assertTrue(framework.getClient().getOauthToken() instanceof VoidOauthToken);
        assertEquals(null, framework.getWallet().loadOauthToken());
        assertPreferencesAreCleared(framework.getStorage());
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
        assertEquals(resourceGenerate.anUser(), framework.getStorage().getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), framework.getStorage().getLanguageCollection());
    }

    protected void assertAccountAndLanguageCollectionFromFrameworkAreNull() {
        assertEquals(null, framework.getAccount());
        assertEquals(null, framework.getLanguageCollection());
    }

    protected void assertFrameworkReturnsValidAccountAndLanguageCollection() throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.anUser(), framework.getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), framework.getLanguageCollection());
    }

    protected void assertDateGreaterOrEqualsThan(Date actual, Date compare) {
        assertTrue(actual.compareTo(compare) >= 0);
    }

    protected void setDataForWalletAndPreferences() throws Exception {
        framework.getWallet().saveOauthToken(oauthToken);
        framework.getStorage().setAccount(resourceGenerate.anUser());
        framework.getStorage().setLanguageCollection(resourceGenerate.aLanguageCollection());
    }

    protected void setDataToBeRefreshed() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -2 * Framework.DAYS_BEFORE_RELOAD);
        framework.getStorage().setLastUpdate(cal.getTime());
    }

    protected void setAValidLanguageCollection() throws IOException, TagMyCodeJsonException {
        framework.setLanguageCollection(resourceGenerate.aLanguageCollection());
    }

    protected void setDataAlreadyRefreshed() {
        framework.getStorage().setLastUpdate(new Date());
    }

    protected void setAValidAccountAccount() throws IOException, TagMyCodeJsonException {
        framework.setAccount(resourceGenerate.anUser());
    }

    private void setAccessToken() throws TagMyCodeException {
        framework.getClient().setOauthToken(oauthToken);
    }

}
