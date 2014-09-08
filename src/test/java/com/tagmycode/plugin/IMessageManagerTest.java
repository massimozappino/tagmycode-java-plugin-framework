package com.tagmycode.plugin;


import support.FakeMessageManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IMessageManagerTest {
    @Test
    public void testShowError() {
        FakeMessageManager fakeMessageManager = new FakeMessageManager();
        fakeMessageManager.error("test");
        assertEquals("test", fakeMessageManager.getCurrentMessage());
    }
}
