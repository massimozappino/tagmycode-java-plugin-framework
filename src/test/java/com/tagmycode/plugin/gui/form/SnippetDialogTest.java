package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SnippetDialogTest extends AbstractTest {


    @Test
    @Ignore
    public void populateLanguagesWorksOnGuiThread() throws Exception {
        final Framework framework = createFramework();

        SnippetDialog snippetDialog = createSnippetDialog(framework);
        snippetDialog.populateWithSnippet(resourceGenerate.aSnippet());
        Thread.sleep(800);
        // TODO assert populateLanguages run on eventThread
    }


    @Test
    public void populateSnippetDialog() throws Exception {
        final Framework framework = createFramework();

        SnippetDialog snippetDialog = createSnippetDialog(framework);
        snippetDialog.populateWithSnippet(resourceGenerate.aSnippet());
        Thread.sleep(800);

        assertEquals("code\r\nsecond line", snippetDialog.getCodeEditorPane().getText());
        assertEquals("A simple description", snippetDialog.getDescriptionTextField().getText());
        assertEquals("tag1 tag2 tag3", snippetDialog.getTagsTextField().getText());
    }

    @Test
    public void populateSnippetDialogWithEmptyLanguage() throws Exception {

    }

    @Test
    public void populateSnippetDialogWithExistentLanguageOverridePreferredLanguage() throws Exception {
//        final Framework framework = createFramework();
//
//        LanguageCollection languageCollection = resourceGenerate.aLanguageCollection();
//        Language customLanguage = new Language();
//        customLanguage.setId(88);
//        customLanguage.setName("Custom");
//        customLanguage.setCode("custom");
//        languageCollection.add(customLanguage);
//
//
//        System.out.println(languageCollection);
//        framework.setLanguageCollection(languageCollection);
//        framework.getStorage().setLastLanguageIndex(2);
//
//        SnippetDialog snippetDialog = createSnippetDialog(framework);
//        Thread.sleep(1500);
//
//        assertEquals(2, snippetDialog.getLanguageComboBox().getSelectedIndex());
//        assertEquals(customLanguage, snippetDialog.getLanguageComboBox().getSelectedItem());
//
//        Snippet expectedSnippet = resourceGenerate.aSnippet();
//        snippetDialog.populateWithSnippet(expectedSnippet);
//        Thread.sleep(1500);
//
//        assertEquals(expectedSnippet.getLanguage(), snippetDialog.getLanguageComboBox().getSelectedItem());
    }

    @Test
    public void lastSelectedLanguageWillBeDefaultAfterShowingDialog() throws Exception {
        final Framework framework = createFramework();

        mockClientReturningValidAccountData(framework);
        framework.fetchAndStoreAllData();
        framework.getStorage().setLastLanguageIndex(1);
        framework.getStorage().setPrivateSnippet(true);

        SnippetDialog snippetDialog = createSnippetDialog(framework);
        Thread.sleep(800);

        assertEquals(1, snippetDialog.getLanguageComboBox().getSelectedIndex());
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