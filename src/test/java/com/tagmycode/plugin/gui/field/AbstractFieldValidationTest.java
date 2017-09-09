package com.tagmycode.plugin.gui.field;

import com.tagmycode.plugin.Framework;
import org.junit.Test;
import support.AbstractTestBase;

import javax.swing.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AbstractFieldValidationTest extends AbstractTestBase {

    @Test
    public void testPerformValidation() throws Exception {
        AbstractFieldValidation abstractFieldValidation;
        JTextField fieldMock = mock(JTextField.class);
        Framework spyFramework = createSpyFramework();

        abstractFieldValidation = createAbstractFieldValidation(fieldMock, spyFramework, true);
        assertTrue(abstractFieldValidation.performValidation());
        verify(spyFramework, times(0)).showErrorDialog(eq("Message error"));
        verify(fieldMock, times(0)).requestFocus();

        abstractFieldValidation = createAbstractFieldValidation(fieldMock, spyFramework, false);
        assertFalse(abstractFieldValidation.performValidation());
        verify(spyFramework, times(1)).showErrorDialog(eq("Message error"));
        verify(fieldMock, times(1)).requestFocus();
    }

    private AbstractFieldValidation createAbstractFieldValidation(final JTextField fieldMock, final Framework spyFramework, final boolean flag) {
        return new AbstractFieldValidation(fieldMock, spyFramework) {
            @Override
            protected boolean validate() {
                return flag;
            }

            @Override
            protected String getMessageError() {
                return "Message error";
            }
        };
    }
}