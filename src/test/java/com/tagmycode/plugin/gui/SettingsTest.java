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

    private Framework framework = mock(Framework.class);
    private SettingsForm settingsForm = new SettingsForm(framework, null);

    @Test
    public void getMainPanel() {
        assertEquals(3, settingsForm.getMainComponent().getComponentCount());
    }

    @Test
    public void notLoggedUserGetsLoginPanel() throws Exception {
        assertLoginPanelIsVisible(settingsForm);
    }

    @Test
    public void loggedUserGetsLogoutPanel() throws Exception {
        when(framework.isInitialized()).thenReturn(true);
        when(framework.getAccount()).thenReturn(resourceGenerate.anUser());

        settingsForm = new SettingsForm(framework, null);

        assertLogoutPanelIsVisible(settingsForm);
    }

    @Test
    public void loginShowAuthenticateDialogAndRefreshPanel() {
        settingsForm.getLoginButton().doClick();
        verify(framework, times(1)).showAuthenticateDialog(any(ICallback.class));
        assertLoginPanelIsVisible(settingsForm);
    }

    @Test
    public void logoutCallLogoutMethodAndRefreshPanel() throws Exception {
        when(framework.getAccount()).thenReturn(resourceGenerate.anUser());
        when(framework.getConsole()).thenReturn(new FakeConsole());

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