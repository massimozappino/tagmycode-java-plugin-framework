package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.FileSize;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.GuiUtil;
import com.tagmycode.sdk.DateParser;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class PropertiesForm extends Windowable {
    private JButton buttonCancel;
    private JPanel contentPane;
    private JTextField snippetTitle;
    private JTextField snippetUrlTextField;
    private JCheckBox privateCheckBox;
    private JTextField codeSizeTextField;
    private JTextField modifiedAtTextField;
    private JTextField createdAtTextField;
    private JTextField languageTextField;
    private JCheckBox synchronizedCheckBox;
    private Snippet snippet;

    public PropertiesForm(Framework framework, Snippet snippet) {
        super(framework, framework.getParentFrame());
        this.snippet = snippet;
        defaultInitWindow();
        initWindow();
        populateFields();
    }

    private void populateFields() {
        setTitle(snippet.getTitle());
        snippetTitle.setText(snippet.getTitle());
        languageTextField.setText(snippet.getLanguage().getName());
        createdAtTextField.setText(dateAndTime(snippet.getCreationDate()));
        modifiedAtTextField.setText(dateAndTime(snippet.getUpdateDate()));
        snippetUrlTextField.setText(snippet.getUrl());
        int bytes = 0;
        try {
            bytes = snippet.getCode().getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException ignored) {
        }
        codeSizeTextField.setText(FileSize.toHumanReadable(bytes));
        privateCheckBox.setSelected(snippet.isPrivate());
        synchronizedCheckBox.setSelected(!snippet.isDirty());

    }

    private String dateAndTime(Date date) {
        return new DateParser(date).toDateTimeLocale(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
    }

    @Override
    protected void initWindow() {
        JTextField[] jTextFields = new JTextField[]{snippetTitle, languageTextField, createdAtTextField,
                modifiedAtTextField, codeSizeTextField, snippetUrlTextField};
        for (JTextField field : jTextFields) {
            GuiUtil.makeTransparentTextField(field);
            field.setEditable(false);
        }

        setSize(450, 350);
        setResizable(true);
    }

    @Override
    protected void onOK() {

    }

    @Override
    public JButton getButtonOk() {
        return new JButton();
    }

    @Override
    protected JButton getButtonCancel() {
        return buttonCancel;
    }

    @Override
    public JComponent getMainComponent() {
        return contentPane;
    }


}
