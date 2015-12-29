package com.tagmycode.plugin.gui.form;


import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.SnippetEditorPane;
import com.tagmycode.plugin.gui.operation.CreateSnippetOperation;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.Snippet;

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
    private JComboBox languageComboBox;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel jpanel;
    private JScrollPane scrollPane;

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
    }

    @Override
    protected void initWindow() {
        descriptionTextField.requestFocus();
        getDialog().setSize(650, 450);
        getDialog().setTitle("Create snippet");
        getDialog().setResizable(true);

        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                populateLanguages();
                restorePreferences();
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
    public JPanel getMainPanel() {
        return contentPane;
    }

    private void populateLanguages() {
        if (framework.getLanguageCollection() == null) {
            // TODO add default language Text
//            Language language = new Language();
//            language.setId(48);
//            language.setCode("text");
//            language.setName("Text");
//            languageComboBox.addItem(language);
        } else {
            for (Language l : framework.getLanguageCollection()) {
                languageComboBox.addItem(l);
            }
        }
    }

    private void listenForChanges() {
        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = languageComboBox.getSelectedIndex();
                framework.getPreferences().setLastLanguageIndex(selectedIndex);
            }
        });

        privateSnippetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = privateSnippetCheckBox.isSelected();

                framework.getPreferences().setPrivateSnippet(selected);
            }
        });
    }

    private void restorePreferences() {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                boolean privateSnippet = framework.getPreferences().getPrivateSnippet();
                privateSnippetCheckBox.setSelected(privateSnippet);
                try {
                    int lastLanguageIndex = framework.getPreferences().getLastLanguageIndex();
                    languageComboBox.setSelectedIndex(lastLanguageIndex);
                } catch (Exception e) {
                    framework.getPreferences().setLastLanguageIndex(0);
                }
            }
        });
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public Snippet getSnippet() {
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
}
