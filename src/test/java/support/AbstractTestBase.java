package support;


import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.SnippetsCollection;
import org.junit.After;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractTestBase {
    protected ResourceGenerate resourceGenerate;
    private MemDbService memDbService;

    @After
    public void closeConnections() throws TagMyCodeStorageException, IOException {
        if (memDbService != null) {
            memDbService.close();
        }
    }

    public AbstractTestBase() {
        resourceGenerate = new ResourceGenerate();
    }

    public Framework createFramework() throws Exception {
        return createFramework(createStorageEngineWithData());
    }

    public Framework createFramework(StorageEngine storageEngine) throws Exception {
        FrameworkConfig frameworkConfig = new FrameworkConfig(new FakePasswordKeyChain(), storageEngine.getDbService(), new FakeMessageManager(), new FakeTaskFactory(), new FakeVersion(), null);
        return new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new FakeSecret());
    }

    protected Framework createSpyFramework() throws Exception {
        return Mockito.spy(createFramework());
    }

    protected StorageEngine createStorageEngine() throws SQLException {
        MemDbService dbService = getSingleInstanceOfMemDb();
        return new StorageEngine(dbService);
    }

    protected MemDbService getSingleInstanceOfMemDb() throws SQLException {
        if (memDbService == null) {
            memDbService = new MemDbService("test");
            memDbService.initialize();
        }
        return memDbService;
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

    protected void assertDataIsCleared(Data data) throws SQLException {
        assertEquals(null, data.getAccount());
        assertEquals(new DefaultLanguageCollection(), data.getLanguages());
        assertEquals(new SnippetsCollection(), data.getSnippets());
        assertEquals(null, data.getLastSnippetsUpdate());
        assertTrue(data.isNetworkingEnabled());
    }

    protected void assertStorageIsCleared(StorageEngine storageEngine) throws SQLException {
        assertEquals(0, storageEngine.getDbService().languageDao().countOf());
        assertEquals(0, storageEngine.getDbService().snippetDao().countOf());
        assertEquals(0, storageEngine.getDbService().propertyDao().countOf());
    }

    protected void assertDataIsValid(Data data) throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), data.getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), data.getLanguages());
        assertEquals(resourceGenerate.aSnippetCollection(), data.getSnippets());
        assertEquals(resourceGenerate.aSnippetsLastUpdate(), data.getLastSnippetsUpdate());
        assertEquals(true, data.isNetworkingEnabled());
    }

}
