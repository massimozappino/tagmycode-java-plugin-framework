package com.tagmycode.plugin.gui.form;


import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.plugin.gui.field.AbstractFieldValidation;
import com.tagmycode.plugin.gui.field.CodeFieldValidation;
import com.tagmycode.plugin.gui.field.TitleFieldValidation;
import com.tagmycode.plugin.operation.CreateAndEditSnippetOperation;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import static com.tagmycode.plugin.gui.GuiUtil.setPlaceholder;

public class SnippetDialog extends AbstractDialog {
    private static final String NEW_SNIPPET_TITLE = "New snippet";
    private static final String EDIT_SNIPPET_TITLE = "Edit snippet";
    private final SyntaxSnippetEditor codeEditorPane;

    private JPanel contentPane;
    private JTextField tagsTextField;
    private JCheckBox privateSnippetCheckBox;
    private JTextArea descriptionTextField;
    private JTextField titleBox;
    private JComboBox<Language> languageComboBox;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel tagsPanel;
    private JPanel snippetPane;
    private JPanel jpanel;
    private JScrollPane scrollPane;
    private DefaultComboBoxModel<Language> defaultComboBoxModel;
    private Snippet editableSnippet;
    private CreateAndEditSnippetOperation createAndEditSnippetOperation;
    private boolean isModified = false;

    public SnippetDialog(final Framework framework, Frame parent) {
        super(framework, parent);
        codeEditorPane = new SyntaxSnippetEditor();
        snippetPane.add(codeEditorPane.getMainComponent());
        defaultInitWindow();
        initWindow();
    }

    public void setEditableSnippet(Snippet snippet) {
        getDialog().setTitle(EDIT_SNIPPET_TITLE);
        editableSnippet = snippet;
        populateFieldsWithSnippet(snippet);
    }

    public void populateFieldsWithSnippet(Snippet snippet) {
        titleBox.setText(snippet.getTitle());
        descriptionTextField.setText(snippet.getDescription());
        tagsTextField.setText(snippet.getTags());
        codeEditorPane.setTextWithSnippet(snippet);
        privateSnippetCheckBox.setSelected(snippet.isPrivate());
        selectLanguage(snippet.getLanguage());
    }

    @Override
    protected void initWindow() {
        snippetMarkedAsSaved();

        createAndEditSnippetOperation = new CreateAndEditSnippetOperation(this);
        defaultComboBoxModel = new DefaultComboBoxModel<>();
        languageComboBox.setModel(defaultComboBoxModel);
        descriptionTextField.requestFocus();

        setPlaceholder("Description", descriptionTextField);
        setPlaceholder("tags space separated", tagsTextField);

        getDialog().setSize(650, 450);
        getDialog().setTitle(NEW_SNIPPET_TITLE);
        getDialog().setResizable(true);
        getDialog().setModal(false);

        populateLanguages();
        restorePreferences();

        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                listenForChanges();
            }
        });
    }

    @Override
    public void closeDialog() {
        if (isModified()) {
            showConfirmDialog();
        } else {
            super.closeDialog();
        }
    }

    protected void showConfirmDialog() {
        String message = String.format("Do you want to save changes to \"%s\"?", titleBox.getText());
        int reply = JOptionPane.showConfirmDialog(getDialog(), message, "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);
        switch (reply) {
            case JOptionPane.YES_OPTION:
                onOK();
                break;
            case JOptionPane.NO_OPTION:
                super.closeDialog();
                break;
        }
    }

    @Override
    protected void onOK() {
        if (checkValidForm()) {
            getCreateAndEditSnippetOperation().runWithTask(framework.getTaskFactory(), "Saving snippet");
        }
    }

    @Override
    public JButton getButtonOk() {
        return buttonOK;
    }

    @Override
    protected JButton getButtonCancel() {
        return buttonCancel;
    }

    @Override
    public JComponent getMainComponent() {
        return contentPane;
    }

    private void populateLanguages() {
        for (Language language : framework.getData().getLanguages()) {
            defaultComboBoxModel.addElement(language);
        }
    }

    private void listenForChanges() {
        DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                snippetMarkedAsModified();
            }

            public void removeUpdate(DocumentEvent e) {
                snippetMarkedAsModified();
            }

            public void insertUpdate(DocumentEvent e) {
                snippetMarkedAsModified();
            }
        };
        titleBox.getDocument().addDocumentListener(listener);
        descriptionTextField.getDocument().addDocumentListener(listener);
        codeEditorPane.getTextArea().getDocument().addDocumentListener(listener);
        tagsTextField.getDocument().addDocumentListener(listener);


        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    snippetMarkedAsModified();
                    saveLastLanguageUsed();
                    changeLanguage(getSelectedLanguage());
                } catch (Exception ignored) {
                }
            }
        });

        privateSnippetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = privateSnippetCheckBox.isSelected();

                try {
                    snippetMarkedAsModified();
                    savePrivateSnippetFlag(selected);
                } catch (TagMyCodeStorageException ignored) {
                }
            }
        });
    }

    protected void changeLanguage(Language language) {
        codeEditorPane.changeLanguage(language);
    }

    private void savePrivateSnippetFlag(boolean selected) throws TagMyCodeStorageException {
        framework.getStorageEngine().savePrivateSnippetFlag(selected);
    }

    private void saveLastLanguageUsed() throws TagMyCodeStorageException {
        framework.getStorageEngine().saveLastLanguageUsed(getSelectedLanguage());
    }

    private Language getSelectedLanguage() {
        return (Language) defaultComboBoxModel.getSelectedItem();
    }

    private void restorePreferences() {
        boolean privateSnippet = framework.getStorageEngine().loadPrivateSnippetFlag();
        privateSnippetCheckBox.setSelected(privateSnippet);
        Language lastLanguageUsed;
        lastLanguageUsed = framework.getStorageEngine().loadLastLanguageUsed();
        defaultComboBoxModel.setSelectedItem(lastLanguageUsed);
    }

    public Snippet createSnippetObject() {
        Snippet snippet = new Snippet();
        snippet.setCode(codeEditorPane.getTextArea().getText());
        snippet.setLanguage((Language) languageComboBox.getSelectedItem());
        snippet.setTitle(titleBox.getText());
        snippet.setDescription(descriptionTextField.getText());
        snippet.setTags(tagsTextField.getText());
        snippet.setPrivate(privateSnippetCheckBox.isSelected());
        Date now = new Date();
        Date creationDate = null;
        if (editableSnippet != null) {
            snippet.setLocalId(editableSnippet.getLocalId());
            snippet.setId(editableSnippet.getId());
            creationDate = editableSnippet.getCreationDate();
        }
        snippet.setCreationDate(creationDate == null ? now : creationDate);
        snippet.setUpdateDate(now);
        snippet.setDirty(true);

        return snippet;
    }

    public JComboBox getLanguageComboBox() {
        return languageComboBox;
    }

    public JTextField getTitleBox() {
        return titleBox;
    }

    public SyntaxSnippetEditor getCodeEditorPane() {
        return codeEditorPane;
    }

    public JTextField getTagsTextField() {
        return tagsTextField;
    }

    public JCheckBox getPrivateSnippetCheckBox() {
        return privateSnippetCheckBox;
    }

    public JTextArea getDescriptionTextField() {
        return descriptionTextField;
    }

    public void selectLanguage(Language language) {
        defaultComboBoxModel.setSelectedItem(language);
    }

    public boolean checkValidForm() {
        for (AbstractFieldValidation fieldValidation : createElementValidateList()) {
            if (!fieldValidation.performValidation()) {
                return false;
            }
        }
        return true;
    }

    protected ArrayList<AbstractFieldValidation> createElementValidateList() {
        ArrayList<AbstractFieldValidation> fieldValidations = new ArrayList<AbstractFieldValidation>();

        fieldValidations.add(new TitleFieldValidation(getTitleBox(), framework));
        fieldValidations.add(new CodeFieldValidation(getCodeEditorPane().getTextArea(), framework));

        return fieldValidations;
    }

    public boolean isModified() {
        return isModified;
    }

    public void snippetMarkedAsSaved() {
        isModified = false;
        getButtonOk().setEnabled(false);
    }

    public void snippetMarkedAsModified() {
        this.isModified = true;
        getButtonOk().setEnabled(true);
    }

    public CreateAndEditSnippetOperation getCreateAndEditSnippetOperation() {
        return createAndEditSnippetOperation;
    }
}
