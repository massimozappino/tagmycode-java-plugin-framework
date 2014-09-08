package com.tagmycode.plugin;

import support.FakeGuiThread;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GuiThreadTest {
    @Test
    public void testRun() {
        FakeGuiThread fakeGuiThread = new FakeGuiThread();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FakeGuiThread.hasRan = true;
            }
        };
        fakeGuiThread.execute(runnable);
        assertEquals(true, fakeGuiThread.hasRan());
    }
}
