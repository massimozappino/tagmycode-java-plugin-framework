package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;
import support.ResourceGenerate;

import static org.junit.Assert.assertEquals;

public class SnippetsJListTest {

    @Test
    public void testUpdateWithSnippets() throws Exception {
        SnippetsJList snippetsJList = new SnippetsJList();
        assertEquals(0, snippetsJList.getSnippetsSize());
        ModelCollection<Snippet> snippetsCollection = new ResourceGenerate().aSnippetCollection();
        snippetsJList.updateWithSnippets(snippetsCollection);
        assertEquals(2, snippetsJList.getSnippetsSize());

        snippetsCollection.remove(0);
        snippetsCollection.add(new ResourceGenerate().aSnippet().setTitle("new Snippet 1"));
        snippetsCollection.add(new ResourceGenerate().aSnippet().setTitle("new Snippet 2"));
        snippetsJList.updateWithSnippets(snippetsCollection);

        assertEquals(3, snippetsCollection.size());
        assertEquals(3, snippetsJList.getSnippetsSize());
    }

    @Test
    public void testGetSelectedSnippet() throws Exception {
        SnippetsJList snippetsJList = new SnippetsJList();
        snippetsJList.updateWithSnippets(new ResourceGenerate().aSnippetCollection());
        snippetsJList.setSelectedIndex(0);
        assertEquals("My title", snippetsJList.getSelectedSnippet().getTitle());
    }
}