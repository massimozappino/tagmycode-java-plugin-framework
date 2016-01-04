package com.tagmycode.plugin.gui.form;


import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.SnippetEditorPane;
import com.tagmycode.plugin.gui.operation.CreateSnippetOperation;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.DefaultLanguage;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.Snippet;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnippetDialog extends AbstractDialog {
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

    public SnippetDialog(final Framework framework, String mimeType, Frame parent) {
        super(framework, parent);
        defaultInitWindow();
        initWindow();
        setMimeType(mimeType);
    }

    public void populateWithSnippet(Snippet snippet) {
        titleBox.setText(snippet.getTitle());
        descriptionTextField.setText(snippet.getDescription());
        tagsTextField.setText(snippet.getTags());
        codeEditorPane.setTextWithSnippet(snippet);
        selectLanguage(snippet.getLanguage());
    }

    @Override
    protected void initWindow() {
        defaultComboBoxModel = new DefaultComboBoxModel<Language>();
        languageComboBox.setModel(defaultComboBoxModel);
        descriptionTextField.requestFocus();
        getDialog().setSize(650, 450);
        getDialog().setTitle("Add snippet");
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
        new CreateSnippetOperation(this).runWithTask(framework.getTaskFactory(), "Saving snippet");
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
        if (framework.getLanguageCollection() == null) {
            defaultComboBoxModel.addElement(new DefaultLanguage());
        } else {
            for (Language l : framework.getLanguageCollection()) {
                defaultComboBoxModel.addElement(l);
            }
        }
    }

    private void listenForChanges() {
        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    framework.getData().setLastLanguageUsed(getSelectedLanguage());
                } catch (JSONException ignored) {
                }
            }
        });

        privateSnippetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = privateSnippetCheckBox.isSelected();

                framework.getData().setPrivateSnippet(selected);
            }
        });
    }

    private Language getSelectedLanguage() {
        return (Language) defaultComboBoxModel.getSelectedItem();
    }

    private void restorePreferences() {
        boolean privateSnippet = framework.getData().getPrivateSnippet();
        privateSnippetCheckBox.setSelected(privateSnippet);
        Language lastLanguageUsed;
        try {
            lastLanguageUsed = framework.getData().getLastLanguageUsed();
        } catch (TagMyCodeJsonException e) {
            lastLanguageUsed = new DefaultLanguage();
        }
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

    public JEditorPane getCodeEditorPane() {
        return codeEditorPane;
    }

    public JTextField getTagsTextField() {
        return tagsTextField;
    }

    public JTextField getDescriptionTextField() {
        return descriptionTextField;
    }

    public JTextField getTitleBox() {
        return titleBox;
    }

    public JCheckBox getPrivateSnippetCheckBox() {
        return privateSnippetCheckBox;
    }

    public void selectLanguage(Language language) {

        defaultComboBoxModel.setSelectedItem(language);


    }
}
