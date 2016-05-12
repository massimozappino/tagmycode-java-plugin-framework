package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;


public class SnippetView extends AbstractGui {
    private final SyntaxSnippetEditor syntaxSnippetEditor;
    private JPanel mainPanel;
    private JLabel tags;
    private JPanel snippetPane;

    public SnippetView(final Snippet snippet) {
        syntaxSnippetEditor = new SyntaxSnippetEditor();
        syntaxSnippetEditor.setEditable(false);
        syntaxSnippetEditor.setTextWithSnippet(snippet);
        snippetPane.add(syntaxSnippetEditor.getMainComponent());
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

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }
}
