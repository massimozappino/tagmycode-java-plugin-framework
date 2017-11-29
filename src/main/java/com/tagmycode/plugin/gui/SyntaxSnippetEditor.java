package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.Snippet;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;

public class SyntaxSnippetEditor implements IAbstractGUI {

    private final RSyntaxTextArea textArea;
    private final RTextScrollPane scrollPane;

    public SyntaxSnippetEditor() {
        textArea = new RSyntaxTextArea(20, 20);
        textArea.setCodeFoldingEnabled(true);
        scrollPane = new RTextScrollPane(textArea);
        scrollPane.setLineNumbersEnabled(false);
        scrollPane.setIconRowHeaderEnabled(false);
        GuiUtil.removeBorder(scrollPane);
    }

    public void applyTheme(Theme theme) {
        if (theme != null) {
            theme.apply(textArea);
        }
    }

    public void setTextWithSnippet(Snippet snippet) {
        setText(snippet.getCode());
        changeLanguage(snippet.getLanguage());
    }

    public void setText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
    }

    private String getMimeFromLanguage(Language language) {
        String code = language.getCode();
        String mimeType = "text/plain";
        switch (code) {
            case "java":
                mimeType = SyntaxConstants.SYNTAX_STYLE_JAVA;
                break;
            case "php":
                mimeType = SyntaxConstants.SYNTAX_STYLE_RUBY;
                break;
            case "bash":
                mimeType = SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
                break;
            case "ruby":
                mimeType = SyntaxConstants.SYNTAX_STYLE_RUBY;
                break;
            case "javascript":
                mimeType = SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
                break;
            case "css":
                mimeType = SyntaxConstants.SYNTAX_STYLE_CSS;
                break;
            case "html":
                mimeType = SyntaxConstants.SYNTAX_STYLE_HTML;
                break;
            case "csharp":
                mimeType = SyntaxConstants.SYNTAX_STYLE_CSHARP;
                break;
            case "sql":
                mimeType = SyntaxConstants.SYNTAX_STYLE_SQL;
                break;
            case "xml":
                mimeType = SyntaxConstants.SYNTAX_STYLE_XML;
                break;
            case "c":
                mimeType = SyntaxConstants.SYNTAX_STYLE_C;
                break;
            case "objc":
                mimeType = SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
                break;
            case "python":
                mimeType = SyntaxConstants.SYNTAX_STYLE_PYTHON;
                break;
            case "perl":
                mimeType = SyntaxConstants.SYNTAX_STYLE_PERL;
                break;
        }
        return mimeType;
    }

    public void changeLanguage(Language language) {
        textArea.setSyntaxEditingStyle(getMimeFromLanguage(language));
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

    public void setPreview() {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }
}
