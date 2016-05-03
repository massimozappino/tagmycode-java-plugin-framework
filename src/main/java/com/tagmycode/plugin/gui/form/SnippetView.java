package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.SnippetEditorPane;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

import static com.tagmycode.plugin.gui.GuiUtil.setBold;


public class SnippetView extends AbstractGui {
    private JPanel mainPanel;
    private SnippetEditorPane snippetEditorPane;
    private JLabel title;
    private JLabel tags;

    public SnippetView(final Snippet snippet) {
        setBold(title);
        snippetEditorPane.setEditable(false);
        snippetEditorPane.setTextWithSnippet(snippet);
        getTitle().setText(snippet.getTitle());

        if (snippet.getTags().length() == 0) {
            tags.setText("no tags");
        } else {
            tags.setText(snippet.getTags());
        }
        initPopupMenuForJTextComponents(getMainComponent());
    }

    public SnippetEditorPane getSnippetEditorPane() {
        return snippetEditorPane;
    }

    public JLabel getTitle() {
        return title;
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }
}
