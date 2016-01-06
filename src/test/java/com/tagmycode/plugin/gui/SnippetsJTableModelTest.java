package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Test;
import support.ResourceGenerate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SnippetsJTableModelTest extends AbstractTest {

    @Test
    public void testGetSnippetAt() throws Exception {
        SnippetsJTableModel snippetsJTableModel = new SnippetsJTableModel();
        SnippetCollection snippets = new SnippetCollection();
        snippets.add(new ResourceGenerate().aSnippet());
        snippetsJTableModel.updateWithSnippets(snippets);

        assertNull(snippetsJTableModel.getSnippetAt(-1));
        assertEquals(new ResourceGenerate().aSnippet(), snippetsJTableModel.getSnippetAt(0));
        assertNull(snippetsJTableModel.getSnippetAt(1));
    }
}