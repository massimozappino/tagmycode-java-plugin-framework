package com.tagmycode.plugin;


import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.model.Snippet;
import org.mockito.Mockito;
import support.*;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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

        TagMyCode mockTagMyCode = getMockedTagMyCode(framework);
        when(mockTagMyCode.fetchAccount()).thenReturn(resourceGenerate.anUser());
        when(mockTagMyCode.fetchLanguages()).thenReturn(resourceGenerate.aLanguageCollection());
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

    protected void assertConsoleMessageContains(IConsole console, String expected) {
        assertTrue(console.getFullLog().contains(expected));
    }
}
