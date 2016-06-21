package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SettingsFormTest extends AbstractTest {

    @Test
    public void fillData() throws Exception {
        Framework framework = createFramework();
        framework.start();
        SettingsForm settingsForm = new SettingsForm(framework, null);
        settingsForm.display();
        assertEquals(resourceGenerate.aUser().getEmail(), settingsForm.email.getText());
    }
}