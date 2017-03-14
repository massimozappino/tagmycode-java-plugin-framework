package com.tagmycode.plugin.gui.field;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CodeFieldValidationTest extends AbstractTest {

    @Test
    public void testValidate() throws Exception {
        Framework framework = createFramework();
        mockTagMyCodeReturningValidAccountData(framework);
        JEditorPane field = spy(new JEditorPane());
        CodeFieldValidation codeFieldValidation = new CodeFieldValidation(field, framework);

        field.setText(".");
        assertTrue(codeFieldValidation.validate());
        verify(field, times(0)).requestFocus();

        field.setText("");
        assertFalse(codeFieldValidation.validate());
        verify(field, times(1)).requestFocus();
    }

}