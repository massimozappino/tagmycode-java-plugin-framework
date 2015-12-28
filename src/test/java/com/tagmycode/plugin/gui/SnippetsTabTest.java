package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import org.junit.Test;
import support.ResourceGenerate;

import javax.swing.*;

import static org.junit.Assert.assertEquals;

public class SnippetsTabTest extends AbstractTest {

    @Test
    public void testSelection() throws Exception {
        SnippetsTab snippetsTab = new SnippetsTab(createFramework());
        SnippetsJList snippetsJList = snippetsTab.getSnippetsJList();
        snippetsJList.updateWithSnippets(new ResourceGenerate().aSnippetCollection());
        JPanel snippetViewFormPanel = snippetsTab.getSnippetViewFormPanel();

        assertEquals(0, snippetViewFormPanel.getComponentCount());

        snippetsJList.setSelectedIndex(0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());

        snippetsJList.setSelectedIndex(1);
        snippetsJList.setSelectedIndex(0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());
    }
}