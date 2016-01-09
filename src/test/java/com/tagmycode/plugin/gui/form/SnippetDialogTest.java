package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.sdk.model.DefaultLanguage;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;

import static org.junit.Assert.*;

public class SnippetDialogTest extends AbstractTest {

    @Test
    public void populateSnippetDialog() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog(createFramework());
        snippetDialog.populateWithSnippet(resourceGenerate.aSnippet());
        Thread.sleep(800);

        assertEquals("code\r\nsecond line", snippetDialog.getCodeEditorPane().getText());
        assertEquals("A simple description", snippetDialog.getDescriptionTextField().getText());
        assertEquals("tag1 tag2 tag3", snippetDialog.getTagsTextField().getText());
    }

    @Test
    public void testCreateSnippetObjectForEmptySnippet() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog(createFramework());
        assertTrue(snippetDialog.createSnippetObject().toJson().length() > 0);
    }

    @Test
    public void testCreateSnippetObjectForSyncSnippet() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog(createFramework());
        Snippet snippet = resourceGenerate.aSnippet();

        snippetDialog.populateWithSnippet(snippet);
        assertEquals(snippet.getCreationDate(), snippetDialog.createSnippetObject().getCreationDate());
        assertEquals(snippet.getUpdateDate(), snippetDialog.createSnippetObject().getUpdateDate());

        snippet.setCreationDate(null);
        snippet.setUpdateDate(null);

        snippetDialog.populateWithSnippet(snippet);
        assertNotNull(snippetDialog.createSnippetObject().getCreationDate());
        assertNotNull(snippetDialog.createSnippetObject().getUpdateDate());
    }

    @Test
    public void populateSnippetDialogWithEmptyLanguage() throws Exception {
        final Framework framework = createFramework();
        SnippetDialog snippetDialog = createSnippetDialog(framework);
        assertEquals(1, snippetDialog.getLanguageComboBox().getItemCount());
        assertEquals(new DefaultLanguage(), snippetDialog.getLanguageComboBox().getItemAt(0));
    }

    @Test
    public void populateSnippetDialogWithExistentLanguageOverridePreferredLanguage() throws Exception {
        final Framework framework = createFramework();

        LanguageCollection languageCollection = resourceGenerate.aLanguageCollection();
        Language customLanguage = new Language();
        customLanguage.setId(88);
        customLanguage.setName("Custom");
        customLanguage.setCode("custom");
        languageCollection.add(customLanguage);

        framework.setLanguageCollection(languageCollection);
        framework.getStorageEngine().saveLastLanguageUsed(customLanguage);

        SnippetDialog snippetDialog = createSnippetDialog(framework);

        assertEquals(customLanguage, snippetDialog.getLanguageComboBox().getSelectedItem());

        Snippet expectedSnippet = resourceGenerate.aSnippet();
        snippetDialog.populateWithSnippet(expectedSnippet);

        assertEquals(expectedSnippet.getLanguage(), snippetDialog.getLanguageComboBox().getSelectedItem());
    }

    @Test
    public void lastSelectedLanguageWillBeDefaultAfterShowingDialog() throws Exception {
        final Framework framework = createFramework();

        mockClientReturningValidAccountData(framework);
        framework.fetchAndStoreAllData();
        framework.getStorageEngine().saveLastLanguageUsed(resourceGenerate.aLanguage());
        framework.getStorageEngine().savePrivateSnippetFlag(true);

        SnippetDialog snippetDialog = createSnippetDialog(framework);
        Thread.sleep(800);

        assertEquals(resourceGenerate.aLanguage(), snippetDialog.getLanguageComboBox().getSelectedItem());
    }

    @Test
    public void createSnippetWriteOnConsole() throws Exception {
        final Framework framework = createFramework();
        mockClientCreateASnippet(framework);

        SnippetDialog snippetDialog = createSnippetDialog(framework);
        snippetDialog.getButtonOk().doClick();
        Thread.sleep(500);

        assertConsoleMessageContains(framework.getConsole(), "https://tagmycode.com/snippet/1");
    }

    private SnippetDialog createSnippetDialog(Framework framework) {
        return new SnippetDialog(framework, null, null);
    }
}