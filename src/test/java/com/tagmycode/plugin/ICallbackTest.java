package com.tagmycode.plugin;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ICallbackTest {
    @Test
    public void testDoOperation() {
        final boolean[] flag = new boolean[1];
        ICallback iCallback = new ICallback() {
            @Override
            public void doOperation() {
                flag[0] = true;
            }
        };
        iCallback.doOperation();
        assertTrue(flag[0]);
    }

}