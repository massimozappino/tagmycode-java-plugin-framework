package com.tagmycode.plugin;

import org.junit.Test;
import support.AbstractTestBase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class DataTest extends AbstractTestBase {

    @Test
    public void testClearDataAndStorage() throws Exception {
        StorageEngine storageSpy = spy(createStorageEngine());
        Data data = new Data(storageSpy);
        data.setAccount(resourceGenerate.aUser());
        data.setLanguages(resourceGenerate.aLanguageCollection());
        data.setSnippets(resourceGenerate.aSnippetCollection());
        data.setNetworkingEnabled(true);

        data.clearDataAndStorage();

        assertDataIsCleared(data);
        assertStorageIsCleared(data.getStorageEngine());
    }

    @Test
    public void testLoadFromStorage() throws Exception {
        Data data = new Data(createStorageEngineWithData());

        data.loadAll();

        assertDataIsValid(data);
    }

    @Test
    public void testReset() throws Exception {
        Data data = new Data(mock(StorageEngine.class));

        data.setAccount(resourceGenerate.aUser());
        data.setLanguages(resourceGenerate.aLanguageCollection());
        data.setSnippets(resourceGenerate.aSnippetCollection());
        data.setLastSnippetsUpdate(resourceGenerate.aSnippetsLastUpdate());
        data.setNetworkingEnabled(false);

        data.reset();

        assertDataIsCleared(data);
    }

    @Test
    public void testSaveAll() throws Exception {
        StorageEngine storage = createStorageEngine();
        Data data = new Data(storage);

        data.setAccount(resourceGenerate.aUser());
        data.setLanguages(resourceGenerate.aLanguageCollection());
        data.setSnippets(resourceGenerate.aSnippetCollection());
        data.setLastSnippetsUpdate(resourceGenerate.aSnippetsLastUpdate());
        data.setNetworkingEnabled(true);
        data.saveAll();

        data.reset();
        data.loadAll();
        assertDataIsValid(data);
    }
}