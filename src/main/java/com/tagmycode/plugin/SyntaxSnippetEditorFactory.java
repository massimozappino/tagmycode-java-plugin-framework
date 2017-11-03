package com.tagmycode.plugin;

import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.plugin.gui.ThemeItem;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyntaxSnippetEditorFactory {
    public static final int DEFAULT_FONT_SIZE = 13;
    public static final String THEME_STRING_DARK = "monokai.xml";
    private Theme theme = null;
    private List<SyntaxSnippetEditor> editorList = new ArrayList<>();
    private int fontSize;

    public ArrayList<ThemeItem> createThemeArray() {
        final ArrayList<ThemeItem> themes = new ArrayList<>();
        themes.add(new ThemeItem("default.xml", "Default"));
        themes.add(new ThemeItem("idea.xml", "IntelliJ IDEA"));
        themes.add(new ThemeItem("eclipse.xml", "Eclipse"));
        themes.add(new ThemeItem("vs.xml", "Visual Studio"));
        themes.add(new ThemeItem("monokai.xml", "Dark Monokai"));
        themes.add(new ThemeItem("dark.xml", "Dark Obsidian PyCs"));
        return themes;
    }

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
        changeThemeString(themeFile);
        changeFontSize(fontSize);
    }

    private void changeThemeString(String themeFile) {
        this.theme = loadTheme(themeFile);
        changeTheme(theme);
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

    public void setDefaultDarkTheme() {
        changeThemeString(THEME_STRING_DARK);
    }
}
