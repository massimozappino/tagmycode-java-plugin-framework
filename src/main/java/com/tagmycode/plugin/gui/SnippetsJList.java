package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.*;

public class SnippetsJList extends AbstractSnippetsListGui {
    private final SnippetsListModel snippetsListModel;
    private final JScrollPane scrollPane;
    private final JList<Snippet> list;
    private DisabledItemSelectionModel disabledItemSelectionModel;
    private DefaultListSelectionModel defaultListSelectionModel;
    private SnippetRowRenderer snippetRowRenderer;

    public SnippetsJList() {
        disabledItemSelectionModel = new DisabledItemSelectionModel();
        defaultListSelectionModel = new DefaultListSelectionModel();
        snippetRowRenderer = new SnippetRowRenderer();
        snippetsListModel = new SnippetsListModel();
        list = new JList<Snippet>();
        scrollPane = new JScrollPane(list);

        list.setCellRenderer(snippetRowRenderer);
        list.setModel(snippetsListModel);
    }

    @Override
    public void updateWithSnippets(SnippetCollection snippets) {
        snippetsListModel.clear();
        if (snippets.size() == 0) {
            list.setSelectionModel(disabledItemSelectionModel);
            snippetsListModel.addElement(new CustomTextSnippetItem("No snippets"));
        } else {
            list.setSelectionModel(defaultListSelectionModel);
            for (Snippet snippet : snippets) {
                snippetsListModel.addElement(snippet);
            }
        }
    }

    @Override
    public Snippet getSelectedSnippet() {
        Snippet selectedSnippet = null;
        if (!list.isSelectionEmpty()) {
            selectedSnippet = snippetsListModel.getElementAt(list.getSelectedIndex());
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
        if (list.getSelectionModel() == disabledItemSelectionModel) {
            size = 0;
        } else {
            size = snippetsListModel.getSize();
        }
        return size;
    }

    @Override
    public JComponent getMainComponent() {
        return scrollPane;
    }

    @Override
    public JList<Snippet> getSnippetsComponent() {
        return list;
    }
}
