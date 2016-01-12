package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.ClipboardCopy;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SnippetView extends AbstractGui {
    private JButton copyButton;
    private JPanel mainPanel;
    private JEditorPane snippetEditorPane;
    private JLabel title;
    private ClipboardCopy clipboardCopy;

    public SnippetView(Snippet snippet) {
        clipboardCopy = new ClipboardCopy();
        snippetEditorPane.setEditable(false);
        snippetEditorPane.setText(snippet.getCode());
        getTitle().setText(snippet.getTitle());

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardCopy.copy(snippetEditorPane.getText());
            }
        });
        initPopupMenuForJTextComponents(getMainComponent());
    }

    public JEditorPane getSnippetEditorPane() {
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
