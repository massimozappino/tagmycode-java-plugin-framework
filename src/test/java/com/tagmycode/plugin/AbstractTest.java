package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguage;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Assert;
import org.mockito.Mockito;
import support.*;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractTest {
    protected ResourceGenerate resourceGenerate;

    public AbstractTest() {
        resourceGenerate = new ResourceGenerate();
    }

    public static void waitForCondition(Condition condition, int attempts) throws Exception {
        while (attempts-- > 0) {
            if (condition.eval()) {
                return;
            }
            Thread.sleep(100);
        }

        Assert.fail("condition was false");
    }

    public Framework createFramework() throws Exception {
        Data data = new Data(new StorageEngine(new FakeStorage()));
        data.setAccount(resourceGenerate.aUser());
        data.setSnippets(new SnippetCollection());
        return createFramework(data);
    }

    public Framework createFramework(Data data) throws Exception {
        FrameworkConfig frameworkConfig = new FrameworkConfig(new FakePasswordKeyChain(), new FakeStorage(), new FakeMessageManager(), new FakeTaskFactory(), null);
        Framework framework = new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new FakeSecret());

        // TODO move to a tested method
        framework.getData().setSnippets(data.getSnippets());
        framework.getData().setAccount(data.getAccount());
        framework.getData().setLanguages(data.getLanguages());
        framework.getData().setLastSnippetsUpdate(data.getLastSnippetsUpdate());

        return framework;
    }

    protected Framework createSpyFramework() throws Exception {
        return Mockito.spy(createFramework());
    }

    public Data createFullData() throws Exception {
        Data data = new Data(new StorageEngine(new FakeStorage()));
        data.setAccount(resourceGenerate.aUser());
        data.setSnippets(resourceGenerate.aSnippetCollection());
        data.setLastSnippetsUpdate(resourceGenerate.aSnippetsLastUpdate());
        data.setLanguages(resourceGenerate.aLanguageCollection());
        return data;
    }

    protected void mockClientReturningValidAccountData(Framework framework) throws Exception {
        Client mockedClient = getMockedClient(framework);
        when(mockedClient.isAuthenticated()).thenReturn(true);
        Mockito.doNothing().when(mockedClient).fetchOauthToken(anyString());
        when(mockedClient.getOauthToken()).thenReturn(new OauthToken("123", "456"));

        TagMyCode mockTagMyCode = getMockedTagMyCode(framework);
        when(mockTagMyCode.fetchAccount()).thenReturn(resourceGenerate.aUser());
        when(mockTagMyCode.fetchLanguages()).thenReturn(resourceGenerate.aLanguageCollection());
        when(mockTagMyCode.fetchSnippetsCollection()).thenReturn(resourceGenerate.aSnippetCollection());
    }

    protected TagMyCode getMockedTagMyCode(Framework framework) throws NoSuchFieldException, IllegalAccessException {
        TagMyCode mockedTagMyCode = mock(TagMyCode.class);

        Field field = framework.getClass().getDeclaredField("tagMyCode");
        field.setAccessible(true);
        field.set(framework, mockedTagMyCode);
        return mockedTagMyCode;
    }

    protected Client getMockedClient(Framework framework) throws NoSuchFieldException, IllegalAccessException {
        Client mockedClient = mock(Client.class);

        Field field = framework.getClass().getDeclaredField("client");
        field.setAccessible(true);
        field.set(framework, mockedClient);
        return mockedClient;
    }

    protected void assertDataIsReset(Data data) {
        assertEquals(null, data.getAccount());
        assertEquals(new DefaultLanguageCollection(), data.getLanguages());
        assertEquals(new SnippetCollection(), data.getSnippets());
        assertEquals(null, data.getLastSnippetsUpdate());
    }

    protected void assertDataIsValid(Data data) throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), data.getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), data.getLanguages());
        assertEquals(resourceGenerate.aSnippetCollection(), data.getSnippets());
        assertEquals(resourceGenerate.aSnippetsLastUpdate(), data.getLastSnippetsUpdate());
    }

    protected void assertStorageDataIsCleared(StorageEngine storageEngine) throws IOException, TagMyCodeJsonException, TagMyCodeStorageException {
        assertNull(storageEngine.loadAccount());
        LanguageCollection languageCollection = new LanguageCollection();
        languageCollection.add(new DefaultLanguage());
        assertEquals(languageCollection, storageEngine.loadLanguageCollection());
        assertEquals(new DefaultLanguage(), storageEngine.loadLastLanguageUsed());
        assertEquals(new SnippetCollection(), storageEngine.loadSnippets());
        assertFalse(storageEngine.loadPrivateSnippetFlag());
    }

    private interface Condition {
        boolean eval();
    }
}
