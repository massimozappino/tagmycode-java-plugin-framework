package com.tagmycode.plugin;

import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import org.junit.Test;
import support.FakeStorage;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
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

    private void assertDataIsValid(Data data) throws IOException, TagMyCodeJsonException {
        assertEquals(resourceGenerate.aUser(), data.getAccount());
        assertEquals(resourceGenerate.aLanguageCollection(), data.getLanguages());
        assertEquals(resourceGenerate.aSnippetCollection(), data.getSnippets());
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