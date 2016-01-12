package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.field.AbstractFieldValidation;
import com.tagmycode.plugin.gui.field.CodeFieldValidation;
import com.tagmycode.plugin.gui.field.TitleFieldValidation;
import com.tagmycode.sdk.model.DefaultLanguage;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;

import java.util.ArrayList;

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
    public void testCreateSnippetObjectForSnippet() throws Exception {
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

    private SnippetDialog createSnippetDialog(Framework framework) {
        return new SnippetDialog(framework, null, null);
    }

    @Test
    public void checkValidFormIsCalled() {
        final String[] value = {null};
        SnippetDialog snippetDialog = new SnippetDialog(createSpyFramework(), null, null) {
            @Override
            public boolean checkValidForm() {
                value[0] = "OK";
                return false;
            }
        };
        snippetDialog.getButtonOk().doClick();
        assertEquals("OK", value[0]);
    }

    @Test
    public void snippetFormValidation() throws Exception {
        SnippetDialog snippetDialog = new SnippetDialog(createSpyFramework(), null, null);
        assertFalse(snippetDialog.checkValidForm());
        //TODO
    }

    @Test
    public void testElementValidateList() throws Exception {
        SnippetDialog snippetDialog = new SnippetDialog(createSpyFramework(), null, null);
        ArrayList<AbstractFieldValidation> elementValidateList = snippetDialog.createElementValidateList();
        assertEquals(2, elementValidateList.size());
        assertTrue(elementValidateList.get(0) instanceof TitleFieldValidation);
        assertTrue(elementValidateList.get(1) instanceof CodeFieldValidation);

    }
}