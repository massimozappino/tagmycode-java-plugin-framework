package com.tagmycode.plugin.gui.form;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertTrue;

public class ConsoleTabTest {
    private boolean actual;

    @Test
    public void testConstructor() throws Exception {
        new ConsoleTab() {
            public void initPopupMenuForJTextComponents(Container container) {
                actual = true;
            }
        };
        assertTrue(actual);
    }
}