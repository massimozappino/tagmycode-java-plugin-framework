package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;

public class SyntaxSnippetEditor implements IAbstractGUI {

    private final RSyntaxTextArea textArea;
    private final RTextScrollPane scrollPane;

    public SyntaxSnippetEditor() {
        textArea = new RSyntaxTextArea();
        scrollPane = new RTextScrollPane(textArea);
    }

    public void setTextWithSnippet(Snippet snippet) {
        textArea.setText(snippet.getCode());
        textArea.setCaretPosition(0);
    }

    @Override
    public JComponent getMainComponent() {
        return scrollPane;
    }

    public void setEditable(boolean flag) {
        textArea.setEditable(flag);
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }
}
