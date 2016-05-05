package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SyntaxSnippetEditor implements IAbstractGUI {

    private final RSyntaxTextArea textArea;
    private final RTextScrollPane scrollPane;

    public SyntaxSnippetEditor() {
        textArea = new RSyntaxTextArea(20, 20);
        textArea.setCodeFoldingEnabled(true);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
        scrollPane = new RTextScrollPane(textArea);
        scrollPane.setLineNumbersEnabled(false);
        scrollPane.setIconRowHeaderEnabled(false);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
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
