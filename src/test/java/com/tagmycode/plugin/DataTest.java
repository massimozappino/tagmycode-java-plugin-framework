package com.tagmycode.plugin;

import org.junit.Test;
import support.FakeStorage;

import static org.mockito.Mockito.spy;

public class DataTest extends AbstractTest {


    @Test
    public void testLoadLanguageCollection() throws Exception {
        StorageEngine storageSpy = spy(new StorageEngine(new FakeStorage()));
        Data data = new Data(storageSpy);
        data.setAccount(resourceGenerate.aUser());
        data.setLanguages(resourceGenerate.aLanguageCollection());
        data.setSnippets(resourceGenerate.aSnippetCollection());

        data.reset();

        assertDataIsReset(data);
    }

    @Test
    public void testLoadFromStorage() throws Exception {
        StorageEngine storage = new StorageEngine(new FakeStorage());
        storage.saveAccount(resourceGenerate.aUser());
        storage.saveLanguageCollection(resourceGenerate.aLanguageCollection());
        storage.saveSnippets(resourceGenerate.aSnippetCollection());
        Data data = new Data(storage);

        data.loadAll();

        assertDataIsValid(data);
    }

    @Test
    public void testSaveAll() throws Exception {
        StorageEngine storage = new StorageEngine(new FakeStorage());
        Data data = new Data(storage);

        data.setAccount(resourceGenerate.aUser());
        data.setLanguages(resourceGenerate.aLanguageCollection());
        data.setSnippets(resourceGenerate.aSnippetCollection());

        data.saveAll();

        data.reset();
        data.loadAll();
        assertDataIsValid(data);
    }
}