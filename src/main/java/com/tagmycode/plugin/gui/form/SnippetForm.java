package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.gui.ClipboardCopy;
import com.tagmycode.plugin.gui.IAbstractGUI;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SnippetForm implements IAbstractGUI {
    private JButton copyButton;
    private JPanel mainPanel;
    private JEditorPane snippetEditorPane;
    private JLabel title;
    private ClipboardCopy clipboardCopy;

    public SnippetForm(Snippet snippet) {
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
    }

    public JEditorPane getSnippetEditorPane() {
        return snippetEditorPane;
    }

    public JLabel getTitle() {
        return title;
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }
}
