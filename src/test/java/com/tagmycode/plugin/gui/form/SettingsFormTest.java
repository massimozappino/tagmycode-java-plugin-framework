package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.StorageEngine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SettingsFormTest extends AbstractTest {

    @Test
    public void fillData() throws Exception {
        Data data = new Data(mock(StorageEngine.class));
        data.setAccount(resourceGenerate.aUser());
        SettingsForm settingsForm = new SettingsForm(createFramework(data), null);
        settingsForm.display();
        assertEquals(resourceGenerate.aUser().getEmail(), settingsForm.email.getText());
    }
}