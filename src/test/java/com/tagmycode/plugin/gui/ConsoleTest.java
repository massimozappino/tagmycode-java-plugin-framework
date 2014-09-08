package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Console;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertTrue;

public class ConsoleTest {

    @Test
    public void testLog() throws Exception {
        Console console = new Console(new JEditorPane());
        console.log("123");

        assertTrue(console.getValue().contains("123"));
    }


}