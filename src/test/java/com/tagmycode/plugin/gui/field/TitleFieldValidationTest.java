package com.tagmycode.plugin.gui.field;

import org.junit.Test;
import support.AbstractTestBase;

import javax.swing.*;

import static org.junit.Assert.assertEquals;

public class TitleFieldValidationTest extends AbstractTestBase {

    @Test
    public void testValidate() throws Exception {
        assertValidationFor("Valid title", "Valid title", true);
        assertValidationFor("12", "12", true);
        assertValidationFor(" 12 ", "12", true);

        assertValidationFor(" a", "a", false);

        assertValidationFor("", "", false);
        assertValidationFor(" ", "", false);
    }

    protected void assertValidationFor(String value, String transformed, boolean expect) throws Exception {
        JTextField jTextField = new JTextField();
        TitleFieldValidation titleFieldValidation = new TitleFieldValidation(jTextField, createSpyFramework());

        jTextField.setText(value);
        assertEquals(expect, titleFieldValidation.validate());
        assertEquals(transformed, jTextField.getText());
    }
}