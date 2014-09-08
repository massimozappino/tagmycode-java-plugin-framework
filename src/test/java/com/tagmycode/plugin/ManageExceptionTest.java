package com.tagmycode.plugin;


import com.tagmycode.sdk.exception.TagMyCodeUnauthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.verify;

public class ManageExceptionTest extends AbstractTest {
    private Framework frameworkSpy;

    @Before
    public void initFrameworkSpy() {
        frameworkSpy = createSpyFramework();
    }

    @Test
    public void manageTagMyCodeUnauthorizedException() throws Exception {
        frameworkSpy.manageTagMyCodeExceptions(new TagMyCodeUnauthorizedException());
        verify(frameworkSpy).logoutAndAuthenticateAgain();
    }

}
