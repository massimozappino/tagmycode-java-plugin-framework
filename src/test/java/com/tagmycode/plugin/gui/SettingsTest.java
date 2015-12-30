package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.ICallback;
import com.tagmycode.plugin.gui.form.SettingsForm;
import org.junit.Test;
import support.FakeConsole;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SettingsTest extends AbstractTest {
    @Test
    public void getMainPanel() {
        Framework framework = createFramework();
        SettingsForm settingsForm = new SettingsForm(framework);

        assertEquals(3, settingsForm.getMainComponent().getComponentCount());
    }

    @Test
    public void notLoggedUserGetsLoginPanel() throws Exception {
        Framework framework = createFramework();
        SettingsForm settingsForm = new SettingsForm(framework);

        assertLoginPanelIsVisible(settingsForm);
    }

    @Test
    public void loggedUserGetsLogoutPanel() throws Exception {
        Framework framework = mock(Framework.class);
        when(framework.isInitialized()).thenReturn(true);
        when(framework.getAccount()).thenReturn(resourceGenerate.anUser());

        SettingsForm settingsForm = new SettingsForm(framework);

        assertLogoutPanelIsVisible(settingsForm);
    }

    @Test
    public void loginShowAuthenticateDialogAndRefreshPanel() {
        Framework framework = mock(Framework.class);
        final SettingsForm settingsForm = new SettingsForm(framework);

        settingsForm.getLoginButton().doClick();
        verify(framework, times(1)).showAuthenticateDialog(any(ICallback.class));
        assertLoginPanelIsVisible(settingsForm);
    }

    @Test
    public void logoutCallLogoutMethodAndRefreshPanel() throws Exception {
        Framework framework = mock(Framework.class);
        when(framework.getAccount()).thenReturn(resourceGenerate.anUser());
        when(framework.getConsole()).thenReturn(new FakeConsole());

        SettingsForm settingsForm = new SettingsForm(framework);

        when(framework.isInitialized()).thenReturn(false);
        settingsForm.getLogoutButton().doClick();

        verify(framework, times(1)).logout();
        assertLoginPanelIsVisible(settingsForm);
    }

    private void assertLoginPanelIsVisible(SettingsForm settingsForm) {
        assertTrue(settingsForm.getLoginPanel().isVisible());
        assertFalse(settingsForm.getLogoutPanel().isVisible());
    }

    private void assertLogoutPanelIsVisible(SettingsForm settingsForm) {
        assertFalse(settingsForm.getLoginPanel().isVisible());
        assertTrue(settingsForm.getLogoutPanel().isVisible());
    }
}