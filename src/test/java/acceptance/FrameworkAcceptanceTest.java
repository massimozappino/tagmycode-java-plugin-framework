package acceptance;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FrameworkAcceptanceTest extends AbstractTest {

    private Framework frameworkSpy;

    @Before
    public void initFramework() {
        frameworkSpy = createSpyFramework();
    }

    @Test
    public void notAuthenticatedUserShouldSeeAuthorizationDialog() throws Exception {
        frameworkSpy = createSpyFramework();
        frameworkSpy.canOperate();
        verify(frameworkSpy, times(1)).showAuthenticateDialog();
    }

    @Test
    public void createASnippet() throws Exception {
        frameworkSpy = createSpyFramework();
        frameworkSpy.canOperate();
        verify(frameworkSpy, times(1)).showAuthenticateDialog();
    }
}