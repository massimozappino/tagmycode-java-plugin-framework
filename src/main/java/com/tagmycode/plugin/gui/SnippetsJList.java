package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

public class SnippetsJList extends JList<Snippet> {
    private final SnippetsListModel snippetsListModel;
    private DisabledItemSelectionModel disabledItemSelectionModel;
    private DefaultListSelectionModel defaultListSelectionModel;
    private SnippetRowRenderer snippetRowRenderer;

    public SnippetsJList() {
        disabledItemSelectionModel = new DisabledItemSelectionModel();
        defaultListSelectionModel = new DefaultListSelectionModel();
        snippetRowRenderer = new SnippetRowRenderer();
        snippetsListModel = new SnippetsListModel();
        setCellRenderer(snippetRowRenderer);
        setModel(snippetsListModel);
    }

    public void updateWithSnippets(ModelCollection<Snippet> snippets) {
        snippetsListModel.clear();
        if (snippets.size() == 0) {
            setSelectionModel(disabledItemSelectionModel);
            snippetsListModel.addElement(new CustomTextSnippetItem("No snippets"));
        } else {
            setSelectionModel(defaultListSelectionModel);
            for (Snippet snippet : snippets) {
                snippetsListModel.addElement(snippet);
            }
        }
    }

    public Snippet getSelectedSnippet() {
        Snippet selectedSnippet = null;
        if (!isSelectionEmpty()) {
            selectedSnippet = snippetsListModel.getElementAt(getSelectedIndex());
        }

        return selectedSnippet;
    }

    public SnippetsListModel getSnippetsModel() {
        return snippetsListModel;
    }

    public void clearAll() {
        snippetsListModel.clear();
    }

    public int getSnippetsSize() {
        int size;
        if (getSelectionModel() == disabledItemSelectionModel) {
            size = 0;
        } else {
            size = snippetsListModel.getSize();
        }
        return size;
    }
}
