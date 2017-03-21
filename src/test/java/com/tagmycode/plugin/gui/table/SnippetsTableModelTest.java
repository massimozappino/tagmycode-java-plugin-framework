package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.TableModelSnippetNotFoundException;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Test;
import support.ResourceGenerate;

import static org.junit.Assert.*;

public class SnippetsTableModelTest extends AbstractTest {
    @Test
    public void testGetSnippetAt() throws Exception {
        Data data = new Data(new StorageEngine(createMemDb()));
        SnippetCollection snippets = new SnippetCollection();
        data.setSnippets(snippets);

        SnippetsTableModel snippetsTableModel = new SnippetsTableModel(data);
        snippets.add(new ResourceGenerate().aSnippet());

        snippetsTableModel.fireSnippetsChanged();

        assertGetSnippetAtThrowsException(snippetsTableModel, 1);
        assertGetSnippetAtThrowsException(snippetsTableModel, -1);
        assertEquals(new ResourceGenerate().aSnippet(), snippetsTableModel.getSnippetAt(0));
    }

    private void assertGetSnippetAtThrowsException(SnippetsTableModel snippetsTableModel, int i) {
        try {
            assertNull(snippetsTableModel.getSnippetAt(-i));
            fail("Expected exception");
        } catch (TableModelSnippetNotFoundException ignored) {
        }
    }
}