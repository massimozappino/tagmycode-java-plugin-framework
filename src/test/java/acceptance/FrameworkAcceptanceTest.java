package acceptance;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.ICallback;
import com.tagmycode.plugin.gui.form.AuthorizationDialog;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FrameworkAcceptanceTest extends AbstractTest {


    @Test
    public void notAuthenticatedUserShouldSeeAuthorizationDialog() throws Exception {
        Framework frameworkSpy = createSpyFramework();
        frameworkSpy.canOperate();
        verify(frameworkSpy, times(1)).showAuthorizationDialog();
    }

    @Test
    @Ignore
    public void afterLoginIShouldSeeMySnippets() throws Exception {
        Framework framework = createFramework(createFullData());
        mockClientReturningValidAccountData(framework);
        Framework frameworkSpy = spy(framework);

        AuthorizationDialog authorizationDialog = frameworkSpy.showAuthorizationDialog();
        String verificationCode = "verification-code";
        authorizationDialog.getVerificationCodeTextField().setText(verificationCode);

        authorizationDialog.getButtonOk().doClick();

        verify(frameworkSpy, times(1)).initialize(verificationCode, new ICallback[0]);

        assertDataIsValid(frameworkSpy.getData());
        assertEquals(2, frameworkSpy.getMainWindow().getSnippetsTab().getSnippetsTable().getSnippetsComponent().getRowCount());
    }

}