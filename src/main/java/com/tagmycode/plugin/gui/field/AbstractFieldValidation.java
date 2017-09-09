package com.tagmycode.plugin.gui.field;

import com.tagmycode.plugin.Framework;

import javax.swing.text.JTextComponent;

public abstract class AbstractFieldValidation {
    protected JTextComponent field;
    private Framework framework;

    public AbstractFieldValidation(JTextComponent field, Framework framework) {
        this.field = field;
        this.framework = framework;
    }

    protected abstract boolean validate();

    protected abstract String getMessageError();

    public boolean performValidation() {
        if (!validate()) {
            framework.showErrorDialog(getMessageError());
            field.requestFocus();
            return false;
        }
        return true;
    }
}