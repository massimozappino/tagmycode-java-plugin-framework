package com.tagmycode.plugin;

import support.FakeConsole;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IConsoleTest {
    @Test
    public void testLog() {
        FakeConsole fakeConsole = new FakeConsole();
        fakeConsole.log("message");
        assertEquals("message", fakeConsole.getFullLog());
    }
}
