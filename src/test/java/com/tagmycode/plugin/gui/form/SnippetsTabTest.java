package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;
import support.AbstractTestBase;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class SnippetsTabTest extends AbstractTestBase {

    private boolean actual;

    @Test
    public void testConstructor() throws Exception {
        SnippetsPanel snippetsTab = new SnippetsPanel(createFramework()) {
            public void initPopupMenuForJTextComponents(Container container) {
                actual = true;
            }
        };
        assertTrue(actual);
        assertEquals(-1, snippetsTab.selectedRow);
    }

    @Test
    public void testSelection() throws Exception {
        Framework framework = createFramework();
        framework.restoreData();

        SnippetsPanel snippetsTab = new SnippetsPanel(framework);
        SnippetsTable snippetsTable = snippetsTab.getSnippetsTable();

        snippetsTable.fireSnippetsChanged();
        JPanel snippetViewFormPanel = snippetsTab.getSnippetViewFormPane();
        JTable jTable = snippetsTable.getJTable();

        assertEquals(1, snippetViewFormPanel.getComponentCount());
        assertEquals(2, jTable.getRowCount());

        jTable.setRowSelectionInterval(0, 0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());

        jTable.setRowSelectionInterval(1, 0);
        jTable.setRowSelectionInterval(0, 0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());
    }

    @Test
    public void disableEnableButtonsForSnippet() throws Exception {
        SnippetsPanel snippetsTab = new SnippetsPanel(createFramework(createStorageEngineWithData()));
        snippetsTab.disableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsTab, false);
        snippetsTab.enableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsTab, true);
    }

    @Test
    public void testCreateEmptySnippet() throws Exception {
        StorageEngine storage = createStorageEngineWithData();
        storage.saveLastLanguageUsed(resourceGenerate.anotherLanguage());
        Framework framework = spy(createFramework(storage));

        Snippet expectedSnippet = new Snippet();
        expectedSnippet.setLanguage(resourceGenerate.anotherLanguage());
        assertEquals(expectedSnippet, framework.getData().createEmptySnippetWithLastLanguage());
    }

    private void assertSnippetButtonsEnabledAre(SnippetsPanel snippetsTab, boolean flag) {
        assertEquals(flag, snippetsTab.editSnippetButton.isEnabled());
        assertEquals(flag, snippetsTab.deleteSnippetButton.isEnabled());
        assertEquals(flag, snippetsTab.copyButton.isEnabled());
        assertEquals(flag, snippetsTab.openInBrowser.isEnabled());
    }

}