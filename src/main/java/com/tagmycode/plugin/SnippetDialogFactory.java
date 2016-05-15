package com.tagmycode.plugin;

import com.tagmycode.plugin.gui.form.SnippetDialog;

import java.awt.*;

public class SnippetDialogFactory {
    public SnippetDialog create(Framework framework, Frame parentFrame) {
        return new SnippetDialog(framework, parentFrame);
    }
}
