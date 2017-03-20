package com.tagmycode.plugin;


import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.SnippetCollection;
import org.mockito.Mockito;
import support.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractTest {
    protected ResourceGenerate resourceGenerate;

    public AbstractTest() {
        resourceGenerate = new ResourceGenerate();
    }

    public Framework createFramework() throws Exception {
        return createFramework(createStorageEngineWithData());
    }

    public Framework createFramework(StorageEngine storageEngine) throws Exception {
        FrameworkConfig frameworkConfig = new FrameworkConfig(new FakePasswordKeyChain(), storageEngine.getDbService(), new FakeMessageManager(), new FakeTaskFactory(), null);
        return new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new FakeSecret(), "test");
    }

    protected Framework createSpyFramework() throws Exception {
        return Mockito.spy(createFramework());
    }

    protected StorageEngine createStorageEngine() throws SQLException {
        MemDbService dbService = new MemDbService();
        dbService.initialize();
        return new StorageEngine(dbService);
    }

    public StorageEngine createStorageEngineWithData() throws Exception {
        StorageEngine storage = createStorageEngine();
        storage.saveAccount(resourceGenerate.aUser());
        storage.saveSnippets(resourceGenerate.aSnippetCollection());
        storage.saveLastSnippetsUpdate(resourceGenerate.aSnippetsLastUpdate());
        storage.saveLanguageCollection(resourceGenerate.aLanguageCollection());
        storage.saveNetworkingEnabledFlag(true);

        return storage;
    }

    protected void mockTagMyCodeReturningValidAccountData(Framework framework) throws Exception {
        TagMyCode mockTagMyCode = getMockedTagMyCode(framework);
        when(mockTagMyCode.isAuthenticated()).thenReturn(true);

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

    protected void assertDataIsReset(Data data) {
        assertEquals(null, data.getAccount());
        assertEquals(new DefaultLanguageCollection(), data.getLanguages());
        assertEquals(new SnippetCollection(), data.getSnippets());
        assertEquals(null, data.getLastSnippetsUpdate());
        assertTrue(data.isNetworkingEnabled());
    }

    protected void assertDataIsValid(Data data) throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), data.getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), data.getLanguages());
        assertEquals(resourceGenerate.aSnippetCollection(), data.getSnippets());
        assertEquals(resourceGenerate.aSnippetsLastUpdate(), data.getLastSnippetsUpdate());
        assertEquals(true, data.isNetworkingEnabled());
    }

}
