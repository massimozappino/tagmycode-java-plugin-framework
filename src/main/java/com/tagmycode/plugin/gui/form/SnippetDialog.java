package com.tagmycode.plugin.gui.form;


import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.SnippetEditorPane;
import com.tagmycode.plugin.gui.field.AbstractFieldValidation;
import com.tagmycode.plugin.gui.field.CodeFieldValidation;
import com.tagmycode.plugin.gui.field.TitleFieldValidation;
import com.tagmycode.plugin.operation.EditSnippetOperation;
import com.tagmycode.plugin.operation.NewSnippetOperation;
import com.tagmycode.plugin.operation.TagMyCodeAsynchronousOperation;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

public class SnippetDialog extends AbstractDialog {
    private static final String NEW_SNIPPET_TITLE = "New snippet";
    private static final String EDIT_SNIPPET_TITLE = "Edit snippet";

    private JPanel contentPane;
    private JTextField tagsTextField;
    private JCheckBox privateSnippetCheckBox;
    private JTextField descriptionTextField;
    private JTextField titleBox;
    private SnippetEditorPane codeEditorPane;
    private JComboBox<Language> languageComboBox;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel jpanel;
    private JScrollPane scrollPane;
    private DefaultComboBoxModel<Language> defaultComboBoxModel;
    private Snippet editableSnippet;
    private NewSnippetOperation newSnippetOperation;
    private EditSnippetOperation editSnippetOperation;

    public SnippetDialog(final Framework framework, String mimeType, Frame parent) {
        super(framework, parent);
        defaultInitWindow();
        initWindow();
        setMimeType(mimeType);
    }

    public void populateWithSnippet(Snippet snippet) {
        getDialog().setTitle(EDIT_SNIPPET_TITLE);
        editableSnippet = snippet;
        titleBox.setText(snippet.getTitle());
        descriptionTextField.setText(snippet.getDescription());
        tagsTextField.setText(snippet.getTags());
        codeEditorPane.setTextWithSnippet(snippet);
        selectLanguage(snippet.getLanguage());
    }

    @Override
    protected void initWindow() {
        newSnippetOperation = new NewSnippetOperation(this);
        editSnippetOperation = new EditSnippetOperation(this);
        defaultComboBoxModel = new DefaultComboBoxModel<Language>();
        languageComboBox.setModel(defaultComboBoxModel);
        descriptionTextField.requestFocus();
        getDialog().setSize(650, 450);
        getDialog().setTitle(NEW_SNIPPET_TITLE);
        getDialog().setResizable(true);

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
    protected void onOK() {
        if (checkValidForm()) {
            getSaveOperation().runWithTask(framework.getTaskFactory(), "Saving snippet");
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
        for (Language l : framework.getLanguageCollection()) {
            defaultComboBoxModel.addElement(l);
        }
    }

    private void listenForChanges() {
        // TODO do not use getStorageEngine
        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    framework.getStorageEngine().saveLastLanguageUsed(getSelectedLanguage());
                } catch (Exception ignored) {
                }
            }
        });

        privateSnippetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = privateSnippetCheckBox.isSelected();

                try {
                    // TODO do not use getStorageEngine
                    framework.getStorageEngine().savePrivateSnippetFlag(selected);
                } catch (TagMyCodeStorageException ignored) {
                }
            }
        });
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
        snippet.setCode(codeEditorPane.getText());
        snippet.setLanguage((Language) languageComboBox.getSelectedItem());
        snippet.setTitle(titleBox.getText());
        snippet.setDescription(descriptionTextField.getText());
        snippet.setTags(tagsTextField.getText());
        snippet.setPrivate(privateSnippetCheckBox.isSelected());
        Date now = new Date();
        Date creationDate = null;
        Date updateDate = null;
        if (editableSnippet != null) {
            snippet.setId(editableSnippet.getId());
            creationDate = editableSnippet.getCreationDate();
            updateDate = editableSnippet.getUpdateDate();
        }
        snippet.setCreationDate(creationDate == null ? now : creationDate);
        snippet.setUpdateDate(updateDate == null ? now : updateDate);

        return snippet;
    }

    public JComboBox getLanguageComboBox() {
        return languageComboBox;
    }

    public void setMimeType(String mimeType) {
        if (mimeType == null) {
            mimeType = "plain/text";
        }
        codeEditorPane.setContentType(mimeType);
    }

    public JTextField getTitleBox() {
        return titleBox;
    }

    public JEditorPane getCodeEditorPane() {
        return codeEditorPane;
    }

    public JTextField getTagsTextField() {
        return tagsTextField;
    }

    public JTextField getDescriptionTextField() {
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
        fieldValidations.add(new CodeFieldValidation(getCodeEditorPane(), framework));

        return fieldValidations;
    }

    public boolean isNewSnippet() {
        return !(editableSnippet != null && editableSnippet.getId() > 0);
    }

    public TagMyCodeAsynchronousOperation<Snippet> getSaveOperation() {
        if (isNewSnippet()) {
            return newSnippetOperation;
        } else {
            return editSnippetOperation;
        }
    }
}
