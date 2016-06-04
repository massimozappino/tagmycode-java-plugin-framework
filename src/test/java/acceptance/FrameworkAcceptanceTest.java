package acceptance;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.LoginDialog;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    public void afterLoginInitializeIsCalled() throws Exception {
        Framework framework = createFramework(createFullData());
        mockClientReturningValidAccountData(framework);
        Framework frameworkSpy = spy(framework);

        final LoginDialog loginDialog = frameworkSpy.showLoginDialog();
        String verificationCode = "verification-code";
        loginDialog.getVerificationCodeTextField().setText(verificationCode);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                loginDialog.getButtonOk().doClick();
            }
        });
        Thread.sleep(200);

        verify(frameworkSpy, times(1)).initialize(verificationCode);
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