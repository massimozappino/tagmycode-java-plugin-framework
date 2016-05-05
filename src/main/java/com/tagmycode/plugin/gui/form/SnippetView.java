package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

import static com.tagmycode.plugin.gui.GuiUtil.setBold;


public class SnippetView extends AbstractGui {
    private final SyntaxSnippetEditor syntaxSnippetEditor;
    private JPanel mainPanel;
    private JLabel title;
    private JLabel tags;
    private JPanel snippetPane;

    public SnippetView(final Snippet snippet) {
        setBold(title);
        syntaxSnippetEditor = new SyntaxSnippetEditor();
        syntaxSnippetEditor.setEditable(false);
        syntaxSnippetEditor.setTextWithSnippet(snippet);
        snippetPane.add(syntaxSnippetEditor.getMainComponent());
        getTitle().setText(snippet.getTitle());

        if (snippet.getTags().length() == 0) {
            tags.setText("no tags");
        } else {
            tags.setText(snippet.getTags());
        }
        initPopupMenuForJTextComponents(getMainComponent());
    }

    public SyntaxSnippetEditor getSnippetEditorPane() {
        return syntaxSnippetEditor;
    }

    public JLabel getTitle() {
        return title;
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }
}
