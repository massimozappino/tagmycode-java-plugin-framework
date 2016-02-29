package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.ClipboardCopy;
import com.tagmycode.plugin.gui.SnippetEditorPane;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SnippetView extends AbstractGui {
    private JButton copyButton;
    private JPanel mainPanel;
    private SnippetEditorPane snippetEditorPane;
    private JLabel title;
    private JButton openInBrowser;
    private JLabel tags;
    private ClipboardCopy clipboardCopy;

    public SnippetView(final Snippet snippet) {
        clipboardCopy = new ClipboardCopy();
        snippetEditorPane.setEditable(false);
        snippetEditorPane.setTextWithSnippet(snippet);
        getTitle().setText(snippet.getTitle());

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardCopy.copy(snippetEditorPane.getText());
            }
        });
        openInBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Browser().openUrl(snippet.getUrl());
            }
        });
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
