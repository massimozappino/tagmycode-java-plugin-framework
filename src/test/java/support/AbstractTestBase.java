package support;


import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguageCollection;
import com.tagmycode.sdk.model.SnippetsCollection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class AbstractTestBase {

    protected ResourceGenerate resourceGenerate;

    public AbstractTestBase() {
        resourceGenerate = new ResourceGenerate();
    }

    public Framework createFramework() throws Exception {
        return createFramework(createStorageEngineWithData());
    }

    public Framework createFramework(StorageEngine storageEngine) throws Exception {
        return new FrameworkBuilder().setStorageEngine(storageEngine).build();
    }

    protected Framework createSpyFramework() throws Exception {
        return spy(createFramework());
    }

    protected StorageEngine createStorageEngine() throws SQLException, TagMyCodeJsonException, TagMyCodeStorageException, IOException {
        return new StorageEngineBuilder().build();
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
        new TagMyCodeMockBuilder(framework).setSnippets(resourceGenerate.aSnippetCollection());
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

    protected void waitForEquals(final Object expected, final Object actual) {
        await().atMost(2, SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return expected.equals(actual);
            }
        });
    }

    protected void waitForFalse(final boolean condition) {
        await().atMost(2, SECONDS).untilFalse(new AtomicBoolean(condition));
    }

    protected void waitForTrue(final boolean condition) {
        await().atMost(2, SECONDS).untilTrue(new AtomicBoolean(condition));

    }
}
