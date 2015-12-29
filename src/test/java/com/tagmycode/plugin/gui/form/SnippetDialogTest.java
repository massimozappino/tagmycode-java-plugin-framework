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
        // TODO: private and languages should not depends on preferences
    }


    @Test
    public void lastSelectedLanguageWillBeDefaultAfterShowingDialog() throws Exception {
        final Framework framework = createFramework();

        mockClientReturningValidAccountData(framework);
        framework.fetchAndStoreAllData();
        framework.getPreferences().setLastLanguageIndex(1);
        framework.getPreferences().setPrivateSnippet(true);

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
        return new SnippetDialog(framework, "", null);
    }
}