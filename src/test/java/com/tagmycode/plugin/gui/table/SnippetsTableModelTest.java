package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Test;
import support.FakeStorage;
import support.ResourceGenerate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SnippetsTableModelTest extends AbstractTest {

    @Test
    public void testGetSnippetAt() throws Exception {
        SnippetsTableModel snippetsTableModel = new SnippetsTableModel(new Data(new StorageEngine(new FakeStorage())));
        SnippetCollection snippets = new SnippetCollection();
        snippets.add(new ResourceGenerate().aSnippet());
        snippetsTableModel.fireSnippetsChanged();

        assertNull(snippetsTableModel.getSnippetAt(-1));
        assertEquals(new ResourceGenerate().aSnippet(), snippetsTableModel.getSnippetAt(0));
        assertNull(snippetsTableModel.getSnippetAt(1));
    }
}