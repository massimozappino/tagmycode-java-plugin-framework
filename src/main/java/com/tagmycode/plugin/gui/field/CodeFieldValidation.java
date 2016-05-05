package com.tagmycode.plugin.gui.field;


import com.tagmycode.plugin.Framework;

import javax.swing.text.JTextComponent;

public class CodeFieldValidation extends AbstractFieldValidation {
    public CodeFieldValidation(JTextComponent field, Framework framework) {
        super(field, framework);
    }

    @Override
    protected boolean validate() {
        if (field.getText().length() <= 0) {
            field.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected String getMessageError() {
        return "Code is empty";
    }
}
