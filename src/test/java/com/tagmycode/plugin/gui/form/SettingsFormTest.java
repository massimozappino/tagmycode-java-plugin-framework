package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import org.junit.Test;
import support.AbstractTestBase;

import static org.junit.Assert.assertEquals;

public class SettingsFormTest extends AbstractTestBase {

    @Test
    public void fillData() throws Exception {
        Framework framework = createFramework();
        framework.start();
        SettingsForm settingsForm = new SettingsForm(framework, null);
        settingsForm.display();
        assertEquals(resourceGenerate.aUser().getEmail(), settingsForm.email.getText());
    }
}