package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

public class SnippetEditorPane extends JTextPane {

    public void setTextWithSnippet(Snippet snippet) {
        setText(snippet.getCode());
        setCaretPosition(0);
    }

    public void clear() {
        setText("");
    }

}
