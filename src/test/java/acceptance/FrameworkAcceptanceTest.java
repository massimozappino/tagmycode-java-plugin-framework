package acceptance;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.LoginDialog;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrameworkAcceptanceTest extends AbstractTest {


    @Test
    public void notAuthenticatedUserShouldSeeAuthorizationDialog() throws Exception {
        Framework frameworkSpy = createSpyFramework();
        frameworkSpy.canOperate();
        assertFalse(frameworkSpy.isInitialized());

        verify(frameworkSpy, times(1)).showLoginDialog();
    }

    @Test
    @Ignore("Wait for polling process")
    public void afterLoginIShouldSeeMySnippets() throws Exception {
        Framework framework = createFramework(createFullData());
        mockClientReturningValidAccountData(framework);
        Framework frameworkSpy = spy(framework);

        LoginDialog loginDialog = frameworkSpy.showLoginDialog();
        String verificationCode = "verification-code";
        loginDialog.getVerificationCodeTextField().setText(verificationCode);

        loginDialog.getButtonOk().doClick();

        verify(frameworkSpy, times(1)).initialize(verificationCode);

        assertDataIsValid(frameworkSpy.getData());
        assertEquals(2, frameworkSpy.getMainWindow().getSnippetsTab().getSnippetsTable().getSnippetsComponent().getRowCount());
    }

    @Test
    public void afterLogoutLSetLastSnippetsUpdateIsCleared() throws Exception {
        Framework framework = createFramework(createFullData());
        mockClientReturningValidAccountData(framework);
        Framework frameworkSpy = spy(framework);
        assertTrue(frameworkSpy.isInitialized());
        assertDataIsValid(framework.getData());

        framework.logout();
        assertDataIsReset(framework.getData());
    }
}