package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Test;
import support.ResourceGenerate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SnippetsTableModelTest extends AbstractTest {

    @Test
    public void testGetSnippetAt() throws Exception {
        SnippetsTableModel snippetsTableModel = new SnippetsTableModel();
        SnippetCollection snippets = new SnippetCollection();
        snippets.add(new ResourceGenerate().aSnippet());
        snippetsTableModel.updateWithSnippets(snippets);

        assertNull(snippetsTableModel.getSnippetAt(-1));
        assertEquals(new ResourceGenerate().aSnippet(), snippetsTableModel.getSnippetAt(0));
        assertNull(snippetsTableModel.getSnippetAt(1));
    }
}