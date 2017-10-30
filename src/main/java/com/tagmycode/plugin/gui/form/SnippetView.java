package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.SyntaxSnippetEditorFactory;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.GuiUtil;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;


public class SnippetView extends AbstractGui {
    private final SyntaxSnippetEditor syntaxSnippetEditor;
    private JPanel mainPanel;
    private JPanel snippetPane;
    private JLabel tagsIconLabel;
    private JPanel tagsContainer;
    private JTextField titleTextField;
    private JTextArea descriptionTextArea;
    private JSplitPane splitPane;

    public SnippetView(SyntaxSnippetEditorFactory syntaxSnippetEditorFactory) {
        getMainComponent().setName("snippet view");
        splitPane.setResizeWeight(.85);
        syntaxSnippetEditor = syntaxSnippetEditorFactory.create();
        syntaxSnippetEditor.setEditable(false);
        snippetPane.add(syntaxSnippetEditor.getMainComponent());
        titleTextField.setEditable(false);
        GuiUtil.removeBorder(titleTextField);
        titleTextField.setOpaque(false);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setLineWrap(true);
        GuiUtil.setPlaceholder("Description", descriptionTextArea);
        initPopupMenuForJTextComponents(getMainComponent());
    }

    public SnippetView setSnippet(Snippet snippet) {
        syntaxSnippetEditor.setTextWithSnippet(snippet);
        titleTextField.setText(snippet.getTitle());
        descriptionTextArea.setText(snippet.getDescription());
        descriptionTextArea.setCaretPosition(0);
        tagsContainer.removeAll();
        if (snippet.getTags().length() == 0) {
            tagsContainer.add(new JLabel("<html><i>no tags</i></html>"));
        } else {
            for (String tag : snippet.getTags().split(" ")) {
                JLabel tagLabel = new JLabel("<html><b><u>" + tag + "</u></b></html>");
                tagsContainer.add(tagLabel);
            }
        }
        return this;
    }

    public SyntaxSnippetEditor getSnippetEditorPane() {
        return syntaxSnippetEditor;
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }
}
