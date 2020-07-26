package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;
import support.AbstractTestBase;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

public class SnippetsPanelTest extends AbstractTestBase {

    private boolean actual;

    @Test
    public void testConstructor() throws Exception {
        SnippetsPanel snippetsPanel = new SnippetsPanel(createFramework()) {
            public void initPopupMenuForJTextComponents(Container container) {
                actual = true;
            }
        };
        assertTrue(actual);
        assertEquals(-1, snippetsPanel.selectedRow);
    }

    @Test
    public void testSelection() throws Exception {
        Framework framework = createFramework();
        framework.restoreData();

        SnippetsPanel snippetsPanel = new SnippetsPanel(framework);
        SnippetsTable snippetsTable = snippetsPanel.getSnippetsTable();

        snippetsTable.fireSnippetsChanged();
        JPanel snippetViewFormPanel = snippetsPanel.getSnippetViewFormPane();
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
        SnippetsPanel snippetsPanel = new SnippetsPanel(createFramework(createStorageEngineWithData()));
        snippetsPanel.disableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsPanel, false);
        snippetsPanel.enableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsPanel, true);
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

    private void assertSnippetButtonsEnabledAre(SnippetsPanel snippetsPanel, boolean flag) {
        assertEquals(flag, snippetsPanel.editSnippetButton.isEnabled());
        assertEquals(flag, snippetsPanel.deleteSnippetButton.isEnabled());
        assertEquals(flag, snippetsPanel.copyButton.isEnabled());
        assertEquals(flag, snippetsPanel.openInBrowser.isEnabled());
    }

}