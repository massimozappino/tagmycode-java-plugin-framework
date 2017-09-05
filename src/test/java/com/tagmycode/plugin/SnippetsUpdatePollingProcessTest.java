package com.tagmycode.plugin;

import org.junit.Test;
import support.AbstractTestBase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SnippetsUpdatePollingProcessTest extends AbstractTestBase {
    @Test
    public void start() throws Exception {
        SnippetsUpdatePollingProcess pollingProcess = new SnippetsUpdatePollingProcess(createFramework());
        pollingProcess.exitStatus = true;
        pollingProcess.start();
        assertTrue(pollingProcess.getThread().isAlive());
        assertFalse(pollingProcess.exitStatus);
    }
}