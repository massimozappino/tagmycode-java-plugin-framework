package com.tagmycode.plugin;


import com.tagmycode.sdk.exception.TagMyCodeUnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.AbstractTestBase;

import static org.mockito.Mockito.verify;

public class ManageExceptionTest extends AbstractTestBase {
    private Framework frameworkSpy;

    @Before
    public void initFrameworkSpy() throws Exception {
        frameworkSpy = createSpyFramework();
    }

    @Test
    public void manageTagMyCodeUnauthorizedException() throws Exception {
        frameworkSpy.manageTagMyCodeExceptions(new TagMyCodeUnauthorizedException());
        verify(frameworkSpy).logoutAndAuthenticateAgain();
    }

}
