package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SnippetsTabTest extends AbstractTest {

    private boolean actual;

    @Test
    public void testConstructor() throws Exception {
        SnippetsTab snippetsTab = new SnippetsTab(createFramework()) {
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

        SnippetsTab snippetsTab = new SnippetsTab(framework);
        SnippetsTable snippetsTable = snippetsTab.getSnippetsTable();
        snippetsTable.fireSnippetsChanged();
        JPanel snippetViewFormPanel = snippetsTab.getSnippetViewFormPane();
        JTable jTable = snippetsTable.getSnippetsComponent();

        assertEquals(0, snippetViewFormPanel.getComponentCount());
        assertEquals(2, jTable.getRowCount());

        jTable.setRowSelectionInterval(0, 0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());

        jTable.setRowSelectionInterval(1, 0);
        jTable.setRowSelectionInterval(0, 0);
        assertEquals(1, snippetViewFormPanel.getComponentCount());
    }

    @Test
    public void disableEnableButtonsForSnippet() throws Exception {
        SnippetsTab snippetsTab = new SnippetsTab(createFramework(createStorageEngineWithData()));
        snippetsTab.disableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsTab, false);
        snippetsTab.enableButtonsForSnippet();
        assertSnippetButtonsEnabledAre(snippetsTab, true);
    }

    @Test
    public void testNewSnippetAction() throws Exception {
        Framework framework = createFramework(createStorageEngineWithData());
        SnippetsTab snippetsTab = spy(new SnippetsTab(framework));

        snippetsTab.newSnippetAction(framework);

        verify(snippetsTab, times(1)).createEmptySnippet(framework);
    }

    @Test
    public void testCreateEmptySnippet() throws Exception {
        StorageEngine storage = createStorageEngineWithData();
        storage.saveLastLanguageUsed(resourceGenerate.anotherLanguage());
        Framework framework = spy(createFramework(storage));
        SnippetsTab snippetsTab = new SnippetsTab(framework);

        Snippet expectedSnippet = new Snippet();
        expectedSnippet.setLanguage(resourceGenerate.anotherLanguage());
        assertEquals(expectedSnippet, snippetsTab.createEmptySnippet(framework));
    }

    private void assertSnippetButtonsEnabledAre(SnippetsTab snippetsTab, boolean flag) {
        assertEquals(flag, snippetsTab.editSnippetButton.isEnabled());
        assertEquals(flag, snippetsTab.deleteSnippetButton.isEnabled());
        assertEquals(flag, snippetsTab.copyButton.isEnabled());
        assertEquals(flag, snippetsTab.openInBrowser.isEnabled());
    }

}