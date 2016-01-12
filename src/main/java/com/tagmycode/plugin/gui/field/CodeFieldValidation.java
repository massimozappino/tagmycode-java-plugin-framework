package com.tagmycode.plugin.gui.field;


import com.tagmycode.plugin.Framework;

import javax.swing.*;

public class CodeFieldValidation extends AbstractFieldValidation {
    public CodeFieldValidation(JEditorPane field, Framework framework) {
        super(field, framework);
    }

    @Override
    protected boolean validate() {
        return false;
    }

    @Override
    protected String getMessageError() {
        return "Code is empty";
    }
}
