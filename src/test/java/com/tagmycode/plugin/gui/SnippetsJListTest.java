package com.tagmycode.plugin.gui;

import org.junit.Test;
import support.ResourceGenerate;

import static org.junit.Assert.assertEquals;

public class SnippetsJListTest {

    @Test
    public void testUpdateWithSnippets() throws Exception {
        SnippetsJList snippetsJList = new SnippetsJList();
        assertEquals(0, snippetsJList.getSnippetsSize());
        snippetsJList.updateWithSnippets(new ResourceGenerate().aSnippetCollection());
        assertEquals(2, snippetsJList.getSnippetsSize());
    }

    @Test
    public void testGetSelectedSnippet() throws Exception {
        SnippetsJList snippetsJList = new SnippetsJList();
        snippetsJList.updateWithSnippets(new ResourceGenerate().aSnippetCollection());
        snippetsJList.setSelectedIndex(0);
        assertEquals("My title", snippetsJList.getSelectedSnippet().getTitle());
    }
}