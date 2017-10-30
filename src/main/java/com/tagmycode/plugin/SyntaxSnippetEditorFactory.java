package com.tagmycode.plugin;

import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyntaxSnippetEditorFactory {
    private Theme theme = null;
    private List<SyntaxSnippetEditor> editorList = new ArrayList<>();
    private int fontSize;


    public SyntaxSnippetEditorFactory(String themeFile, int fontSize) {
        this.fontSize = fontSize;
        this.theme = loadTheme(themeFile);
    }

    private Theme loadTheme(String themeFile) {
        try {
            return Theme.load(SyntaxSnippetEditor.class.getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/" + themeFile));
        } catch (IOException e) {
            return null;
        }
    }

    public SyntaxSnippetEditor create() {
        SyntaxSnippetEditor syntaxSnippetEditor = new SyntaxSnippetEditor();
        editorList.add(syntaxSnippetEditor);
        applyCurrentChanges();
        return syntaxSnippetEditor;
    }

    private void applyCurrentChanges() {
        changeTheme(theme);
        changeFontSize(fontSize);
    }

    public void applyChanges(String themeFile, int fontSize) {
        this.theme = loadTheme(themeFile);
        changeTheme(theme);
        changeFontSize(fontSize);
    }

    private void changeTheme(Theme theme) {
        this.theme = theme;
        for (SyntaxSnippetEditor editor : editorList) {
            editor.applyTheme(this.theme);
        }
    }

    private void changeFontSize(int fontSize) {
        this.fontSize = fontSize;

        for (SyntaxSnippetEditor editor : editorList) {
            Font currentFont = editor.getTextArea().getFont();
            Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), fontSize);
            RSyntaxTextArea textArea = editor.getTextArea();

            textArea.setFont(newFont);
        }
    }

    public int getFontSize() {
        return fontSize;
    }
}
