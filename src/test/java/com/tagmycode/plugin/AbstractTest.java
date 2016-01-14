package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.*;
import org.mockito.Mockito;
import support.*;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractTest {
    protected ResourceGenerate resourceGenerate;

    public AbstractTest() {
        resourceGenerate = new ResourceGenerate();
    }

    public Framework createFramework() {
        FrameworkConfig frameworkConfig = new FrameworkConfig(new FakePasswordKeyChain(), new FakeStorage(), new FakeMessageManager(), new FakeTaskFactory(), null);
        return new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new FakeSecret());
    }

    protected Framework createSpyFramework() {
        return Mockito.spy(createFramework());
    }

    protected void mockClientReturningValidAccountData(Framework framework) throws Exception {
        Client mockedClient = getMockedClient(framework);
        when(mockedClient.isAuthenticated()).thenReturn(true);
        Mockito.doNothing().when(mockedClient).fetchOauthToken(anyString());
        when(mockedClient.getOauthToken()).thenReturn(new OauthToken("123", "456"));

        TagMyCode mockTagMyCode = getMockedTagMyCode(framework);
        when(mockTagMyCode.fetchAccount()).thenReturn(resourceGenerate.aUser());
        when(mockTagMyCode.fetchLanguages()).thenReturn(resourceGenerate.aLanguageCollection());
        when(mockTagMyCode.fetchSnippets()).thenReturn(resourceGenerate.aSnippetCollection());
    }

    protected TagMyCode getMockedTagMyCode(Framework framework) throws NoSuchFieldException, IllegalAccessException {
        TagMyCode mockedTagMyCode = mock(TagMyCode.class);

        Field field = framework.getClass().getDeclaredField("tagMyCode");
        field.setAccessible(true);
        field.set(framework, mockedTagMyCode);
        return mockedTagMyCode;
    }

    protected void mockClientCreateASnippet(Framework framework) throws Exception {
        TagMyCode mockedTagMyCode = getMockedTagMyCode(framework);

        when(mockedTagMyCode.createSnippet(any(Snippet.class))).thenReturn(resourceGenerate.aSnippet());
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
    }

    protected void assertDataIsValid(Data data) throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), data.getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), data.getLanguages());
        assertEquals(resourceGenerate.aSnippetCollection(), data.getSnippets());
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
}
