package com.tagmycode.plugin.gui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ClipboardCopy {
    private Clipboard clipboard;

    public ClipboardCopy() {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public void copy(String text) {
        StringSelection stringSelection = new StringSelection(text);
        clipboard.setContents(stringSelection, stringSelection);
    }
}
