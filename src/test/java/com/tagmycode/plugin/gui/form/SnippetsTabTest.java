package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.gui.SnippetsJList;
import org.junit.Test;
import support.ResourceGenerate;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SnippetsTabTest extends AbstractTest {
    private boolean actual;

    @Test
    public void testConstructor() throws Exception {
        new SnippetsTab(createFramework()) {
            public void initPopupMenuForJTextComponents(Container container) {
                actual = true;
            }
        };
        assertTrue(actual);
    }

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