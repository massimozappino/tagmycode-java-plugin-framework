package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;


public class SnippetView extends AbstractGui {
    private final SyntaxSnippetEditor syntaxSnippetEditor;
    private JPanel mainPanel;
    private JPanel snippetPane;
    private JLabel tagsIconLabel;
    private JPanel tagsContainer;

    public SnippetView(final Snippet snippet) {
        syntaxSnippetEditor = new SyntaxSnippetEditor();
        syntaxSnippetEditor.setEditable(false);
        syntaxSnippetEditor.setTextWithSnippet(snippet);
        snippetPane.add(syntaxSnippetEditor.getMainComponent());
        tagsContainer.removeAll();
        if (snippet.getTags().length() == 0) {
            tagsContainer.add(new JLabel("<html><i>no tags</i></html>"));
        } else {
            for (String tag : snippet.getTags().split(" ")) {
                JLabel tagLabel = new JLabel("<html><b><u>" + tag + "</u></b></html>");
                tagsContainer.add(tagLabel);
            }
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
