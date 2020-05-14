package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.gui.form.LoginDialog;
import org.junit.Test;
import support.FrameworkBuilder;

import javax.swing.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FrameworkAcceptanceTest extends AcceptanceTestBase {

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
        jFrame.pack();

        JPanel snippetViewFormPane = framework.getMainWindow().getSnippetsPanel().getSnippetViewFormPane();
        waitForTrue("welcome view".equals(snippetViewFormPane.getComponent(0).getName()));

        framework.getMainWindow().getSnippetsPanel().getSnippetsTable().getJTable().setRowSelectionInterval(1, 1);

        waitForTrue("snippet view".equals(snippetViewFormPane.getComponent(0).getName()));
    }

    @Test
    public void networkingEnabledAfterRestart() throws Exception {
        StorageEngine storage = createStorageEngine();
        storage.saveNetworkingEnabledFlag(true);
        Framework framework = createFramework(storage);
        mockTagMyCodeReturningValidAccountData(framework);

        framework.start();

        assertTrue(framework.getData().isNetworkingEnabled());
        assertTrue(framework.getMainWindow().getSnippetsPanel().getButtonNetworking().getIcon().toString().contains("/icons/connected.png"));
    }

    @Test
    public void networkingDisabledAfterRestart() throws Exception {
        StorageEngine storageEngineWithData = createStorageEngineWithData();
        storageEngineWithData.saveNetworkingEnabledFlag(false);
        final Framework framework = new FrameworkBuilder().setStorageEngine(storageEngineWithData).build();
        mockTagMyCodeReturningValidAccountData(framework);

        framework.start();

        waitForFalse(framework.getData().isNetworkingEnabled());
        waitForTrue(framework.getMainWindow().getSnippetsPanel().getButtonNetworking().getIcon().toString().contains("/icons/disconnected.png"));
    }
}