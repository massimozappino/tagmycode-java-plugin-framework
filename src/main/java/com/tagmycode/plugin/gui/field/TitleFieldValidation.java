package com.tagmycode.plugin.gui.field;

import com.tagmycode.plugin.Framework;

import javax.swing.*;

public class TitleFieldValidation extends AbstractFieldValidation {

    public TitleFieldValidation(JTextField field, Framework framework) {
        super(field, framework);
    }

    @Override
    protected boolean validate() {
        field.setText(field.getText().trim());
        return (field.getText().length() > 1);
    }

    @Override
    protected String getMessageError() {
        return "Title must be at least 2 characters";
    }

}
