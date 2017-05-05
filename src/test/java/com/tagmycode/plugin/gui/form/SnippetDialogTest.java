package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.field.AbstractFieldValidation;
import com.tagmycode.plugin.gui.field.CodeFieldValidation;
import com.tagmycode.plugin.gui.field.TitleFieldValidation;
import com.tagmycode.plugin.operation.EditSnippetOperation;
import com.tagmycode.plugin.operation.NewSnippetOperation;
import com.tagmycode.sdk.model.DefaultLanguage;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.Snippet;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.Test;
import support.AbstractTestBase;

import javax.swing.*;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SnippetDialogTest extends AbstractTestBase {

    @Test
    public void populateSnippetDialog() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog(createFramework());
        Snippet snippet = resourceGenerate.aSnippet();

        snippetDialog.populateFieldsWithSnippet(snippet);
        Thread.sleep(800);

        assertEquals("code\r\nsecond line", snippetDialog.getCodeEditorPane().getTextArea().getText());
        assertEquals("A simple description", snippetDialog.getDescriptionTextField().getText());
        assertEquals("tag1 tag2 tag3", snippetDialog.getTagsTextField().getText());
        assertFalse(snippetDialog.getPrivateSnippetCheckBox().isSelected());

        snippet = resourceGenerate.aSnippet().setPrivate(true);
        snippetDialog.populateFieldsWithSnippet(snippet);
        assertTrue(snippetDialog.getPrivateSnippetCheckBox().isSelected());
    }

    @Test
    public void testCreateSnippetObjectForEmptySnippet() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog(createFramework());
        Snippet snippetObject = snippetDialog.createSnippetObject();
        assertEquals(0, snippetObject.getId());
        assertTrue(snippetObject.toJson().length() > 0);
        assertEquals("New snippet", snippetDialog.getDialog().getTitle());
        assertTrue(snippetDialog.isNewSnippet());
    }

    @Test
    public void testCreateSnippetObjectForSnippet() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog(createFramework());
        Snippet snippet = resourceGenerate.aSnippet();

        snippetDialog.setEditableSnippet(snippet);
        Snippet snippetObject;

        snippetObject = snippetDialog.createSnippetObject();
        assertEquals(1, snippetObject.getId());
        assertEquals(snippet.getCreationDate(), snippetObject.getCreationDate());
        assertEquals(snippet.getCreationDate(), snippetObject.getCreationDate());
        assertEquals(snippet.getUpdateDate(), snippetObject.getUpdateDate());

        snippet.setCreationDate(null);
        snippet.setUpdateDate(null);

        snippetDialog.setEditableSnippet(snippet);
        snippetObject = snippetDialog.createSnippetObject();
        assertNotNull(snippetObject.getCreationDate());
        assertNotNull(snippetObject.getUpdateDate());

        assertEquals("Edit snippet", snippetDialog.getDialog().getTitle());
        assertFalse(snippetDialog.isNewSnippet());
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

        LanguagesCollection languageCollection = resourceGenerate.aLanguageCollection();
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
        snippetDialog.populateFieldsWithSnippet(expectedSnippet);

        assertEquals(expectedSnippet.getLanguage(), snippetDialog.getLanguageComboBox().getSelectedItem());
    }

    @Test
    public void lastSelectedLanguageWillBeDefaultAfterShowingDialog() throws Exception {
        final Framework framework = createFramework();

        mockTagMyCodeReturningValidAccountData(framework);
        framework.fetchAndStoreAllData();
        framework.getStorageEngine().saveLastLanguageUsed(resourceGenerate.aLanguage());
        framework.getStorageEngine().savePrivateSnippetFlag(true);

        SnippetDialog snippetDialog = createSnippetDialog(framework);
        Thread.sleep(800);

        assertEquals(resourceGenerate.aLanguage(), snippetDialog.getLanguageComboBox().getSelectedItem());
    }

    private SnippetDialog createSnippetDialog(Framework framework) {
        return new SnippetDialog(framework, null);
    }

    @Test
    public void checkValidFormIsCalled() throws Exception {
        final String[] value = {null};
        SnippetDialog snippetDialog = new SnippetDialog(createSpyFramework(), null) {
            @Override
            public boolean checkValidForm() {
                value[0] = "OK";
                return false;
            }
        };
        JButton buttonOk = snippetDialog.getButtonOk();
        assertEquals(false, buttonOk.isEnabled());

        snippetDialog.setSnippetIsModified();
        buttonOk.doClick();

        assertEquals("OK", value[0]);
    }

    @Test
    public void snippetFormValidation() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog();
        assertFalse(snippetDialog.checkValidForm());

        snippetDialog.getTitleBox().setText("a title");
        snippetDialog.getCodeEditorPane().getTextArea().setText("a code");

        assertTrue(snippetDialog.checkValidForm());
    }

    @Test
    public void testElementValidateList() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog();
        ArrayList<AbstractFieldValidation> elementValidateList = snippetDialog.createElementValidateList();
        assertEquals(2, elementValidateList.size());
        assertTrue(elementValidateList.get(0) instanceof TitleFieldValidation);
        assertTrue(elementValidateList.get(1) instanceof CodeFieldValidation);
    }

    @Test
    public void testOnOk() throws Exception {
        SnippetDialog snippetDialog = spy(createSnippetDialog());
        snippetDialog.onOK();

        verify(snippetDialog, times(1)).checkValidForm();
        verify(snippetDialog, times(0)).getSaveOperation();

        snippetDialog.populateFieldsWithSnippet(resourceGenerate.aSnippet());

        snippetDialog.onOK();

        verify(snippetDialog, times(2)).checkValidForm();
        verify(snippetDialog, times(1)).getSaveOperation();
    }

    @Test
    public void testChangeLanguage() throws Exception {
        Framework framework = createFramework();
        framework.restoreData();
        final SnippetDialog snippetDialog = new SnippetDialog(framework, null);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snippetDialog.getLanguageComboBox().setSelectedIndex(0);
            }
        });
        assertEquals(SyntaxConstants.SYNTAX_STYLE_JAVA, snippetDialog.getCodeEditorPane().getTextArea().getSyntaxEditingStyle());
    }

    @Test
    public void testGetSaveOperation() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog();

        assertTrue(snippetDialog.isNewSnippet());
        assertTrue(snippetDialog.getSaveOperation() instanceof NewSnippetOperation);

        snippetDialog.populateFieldsWithSnippet(new Snippet().setId(1));
        assertTrue(snippetDialog.isNewSnippet());
        assertTrue(snippetDialog.getSaveOperation() instanceof NewSnippetOperation);

        snippetDialog.populateFieldsWithSnippet(new Snippet());
        assertTrue(snippetDialog.isNewSnippet());
        assertTrue(snippetDialog.getSaveOperation() instanceof NewSnippetOperation);

        snippetDialog.setEditableSnippet(new Snippet());
        assertTrue(snippetDialog.isNewSnippet());
        assertTrue(snippetDialog.getSaveOperation() instanceof NewSnippetOperation);

        snippetDialog.setEditableSnippet(new Snippet().setId(1));
        assertFalse(snippetDialog.isNewSnippet());
        assertTrue(snippetDialog.getSaveOperation() instanceof EditSnippetOperation);
    }

    @Test
    public void testIsNewSnippet() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog();

        assertTrue(snippetDialog.isNewSnippet());

        snippetDialog.setEditableSnippet(new Snippet());
        assertTrue(snippetDialog.isNewSnippet());

        snippetDialog.setEditableSnippet(new Snippet().setId(1));
        assertFalse(snippetDialog.isNewSnippet());
    }

    @Test
    public void testSetSnippetIsNotModified() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog();
        snippetDialog.snippetMarkedAsSaved();
        assertFalse(snippetDialog.isModified());
        assertFalse(snippetDialog.getButtonOk().isEnabled());
    }

    @Test
    public void isNotModifiedWithoutActions() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog();
        assertFalse(snippetDialog.isModified());
        assertFalse(snippetDialog.getButtonOk().isEnabled());
    }

    @Test
    public void testSetSnippetIsModified() throws Exception {
        SnippetDialog snippetDialog = createSnippetDialog();
        snippetDialog.setSnippetIsModified();
        assertSnippetDialogIsModified(snippetDialog);
        assertTrue(snippetDialog.getButtonOk().isEnabled());
    }

    @Test
    public void isModifiedAfterTitleChanged() throws Exception {
        final SnippetDialog snippetDialog = createSnippetDialog();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snippetDialog.getTitleBox().setText("title");
            }
        });
        assertSnippetDialogIsModified(snippetDialog);
    }

    @Test
    public void isModifiedAfterDescriptionChanged() throws Exception {
        final SnippetDialog snippetDialog = createSnippetDialog();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snippetDialog.getDescriptionTextField().setText("description");
            }
        });
        assertSnippetDialogIsModified(snippetDialog);
    }

    @Test
    public void isModifiedAfterCodeChanged() throws Exception {
        final SnippetDialog snippetDialog = createSnippetDialog();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snippetDialog.getCodeEditorPane().setText("code");
            }
        });
        assertSnippetDialogIsModified(snippetDialog);
    }

    @Test
    public void isModifiedAfterTagsChanged() throws Exception {
        final SnippetDialog snippetDialog = createSnippetDialog();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snippetDialog.getTagsTextField().setText("tags");

            }
        });
        assertSnippetDialogIsModified(snippetDialog);
    }

    @Test
    public void isModifiedAfterPrivateFlagChanged() throws Exception {
        final SnippetDialog snippetDialog = createSnippetDialog();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snippetDialog.getPrivateSnippetCheckBox().doClick();
            }
        });
        assertSnippetDialogIsModified(snippetDialog);
    }

    @Test
    public void isModifiedAfterLanguageChanged() throws Exception {
        Framework framework = createFramework(createStorageEngineWithData());
        framework.restoreData();
        final SnippetDialog snippetDialog = new SnippetDialog(framework, null);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snippetDialog.getLanguageComboBox().setSelectedIndex(1);
            }
        });
        assertSnippetDialogIsModified(snippetDialog);
    }

    private void assertSnippetDialogIsModified(SnippetDialog snippetDialog) {
        assertTrue(snippetDialog.isModified());
    }

    @Test
    public void askToSaveIfModified() throws Exception {
        final boolean[] showConfirmDialogIsCalled = new boolean[1];
        SnippetDialog snippetDialog = new SnippetDialog(createSpyFramework(), null) {
            protected void showConfirmDialog() {
                showConfirmDialogIsCalled[0] = true;
            }
        };
        snippetDialog.setSnippetIsModified();
        snippetDialog.closeDialog();
        assertTrue(showConfirmDialogIsCalled[0]);
    }

    private SnippetDialog createSnippetDialog() throws Exception {
        return new SnippetDialog(createSpyFramework(), null);
    }

}