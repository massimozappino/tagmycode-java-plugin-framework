package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import org.junit.Test;

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
        SnippetsTab snippetsTab = new SnippetsTab(createFramework(createFullData()));
        SnippetsTable snippetsTable = snippetsTab.getSnippetsTable();
        snippetsTable.fireSnippetsChanged();
        JPanel snippetViewFormPanel = snippetsTab.getSnippetViewFormPane();

        assertEquals(0, snippetViewFormPanel.getComponentCount());

        JTable jTable = snippetsTable.getSnippetsComponent();
        assertEquals(2, jTable.getRowCount());

        jTable.setRowSelectionInterval(0, 0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());

        jTable.setRowSelectionInterval(1, 0);
        jTable.setRowSelectionInterval(0, 0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());
    }

    @Test
    public void disableEnableButtonsForSnippet() throws Exception {
        SnippetsTab snippetsTab = new SnippetsTab(createFramework(createFullData()));
        snippetsTab.disableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsTab, false);
        snippetsTab.enableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsTab, true);
    }

    private void assertSnippetButtonsEnabledAre(SnippetsTab snippetsTab, boolean flag) {
        assertEquals(flag, snippetsTab.editSnippetButton.isEnabled());
        assertEquals(flag, snippetsTab.deleteSnippetButton.isEnabled());
        assertEquals(flag, snippetsTab.copyButton.isEnabled());
        assertEquals(flag, snippetsTab.openInBrowser.isEnabled());
    }

}