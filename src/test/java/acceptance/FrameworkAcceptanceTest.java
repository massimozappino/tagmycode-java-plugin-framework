package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.gui.form.LoginDialog;
import org.junit.Test;
import support.AbstractTestBase;

import javax.swing.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrameworkAcceptanceTest extends AbstractTestBase {


    @Test
    public void notAuthenticatedUserShouldSeeAuthorizationDialog() throws Exception {
        Framework frameworkSpy = createSpyFramework();

        frameworkSpy.canOperate();

        assertFalse(frameworkSpy.isInitialized());
        verify(frameworkSpy, times(1)).showLoginDialog();
    }

    @Test
    public void afterLoginInitializeIsCalled() throws Exception {
        Framework framework = createFramework(createStorageEngineWithData());
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
        Framework framework = createFramework(createStorageEngineWithData());
        framework.start();
        mockTagMyCodeReturningValidAccountData(framework);
        Framework frameworkSpy = spy(framework);
        assertTrue(frameworkSpy.isInitialized());
        assertDataIsValid(framework.getData());

        framework.logout();
        assertDataIsCleared(framework.getData());
    }

    @Test
    public void selectASnippetAndSeeDetailsOnRightPanel() throws Exception {
        Framework framework = createFramework(createStorageEngineWithData());
        mockTagMyCodeReturningValidAccountData(framework);
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

    @Test
    public void networkingEnabledAfterRestart() throws Exception {
        StorageEngine storage = createStorageEngineWithData();
        storage.saveNetworkingEnabledFlag(true);
        Framework framework = createFramework(storage);
        mockTagMyCodeReturningValidAccountData(framework);

        framework.start();

        assertTrue(framework.getData().isNetworkingEnabled());
        assertTrue(framework.getMainWindow().getSnippetsTab().getButtonNetworking().getIcon().toString().contains("/icons/connected.png"));
    }

    @Test
    public void networkingDisabledAfterRestart() throws Exception {
        Framework framework = acceptanceFramework();

        framework.start();

        assertFalse(framework.getData().isNetworkingEnabled());
        assertTrue(framework.getMainWindow().getSnippetsTab().getButtonNetworking().getIcon().toString().contains("/icons/disconnected.png"));
    }

    private Framework acceptanceFramework() throws Exception {
        StorageEngine storage = createStorageEngineWithData();
        storage.saveNetworkingEnabledFlag(false);
        Framework framework = createFramework(storage);
        mockTagMyCodeReturningValidAccountData(framework);
        return framework;
    }
}