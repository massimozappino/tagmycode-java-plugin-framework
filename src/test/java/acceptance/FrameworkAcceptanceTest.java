package acceptance;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.LoginDialog;
import org.junit.Test;

import javax.swing.*;

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

    @Test
    public void selectASnippetAndSeeDetailsOnRightPanel() throws Exception {
        Framework framework = createFramework(createFullData());
        mockClientReturningValidAccountData(framework);
        framework.getData().setAccount(resourceGenerate.aUser());
        framework.getData().setSnippets(resourceGenerate.aSnippetCollection());
        framework.getData().saveAll();

        framework.start();

        JFrame jFrame = new JFrame();
        jFrame.add(framework.getMainWindow().getMainComponent());
        jFrame.setVisible(true);
        jFrame.pack();

        JPanel snippetViewFormPane = framework.getMainWindow().getSnippetsTab().getSnippetViewFormPane();
        assertEquals(0, snippetViewFormPane.getComponentCount());

        framework.getMainWindow().getSnippetsTab().getSnippetsTable().getSnippetsComponent().setRowSelectionInterval(1, 1);

        assertEquals(1, snippetViewFormPane.getComponentCount());
    }
}