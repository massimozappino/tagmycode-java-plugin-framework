package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class SnippetsJTableModel extends AbstractTableModel {
    public final static int LANGUAGE = 0;
    public final static int TITLE = 1;

    private Vector<Snippet> snippetVector;
    private String[] columns;
    private Framework framework;

    public SnippetsJTableModel(Framework framework) {
        this.framework = framework;
        snippetVector = new Vector<Snippet>();
        columns = new String[]{"Language", "Title"};
    }

    @Override
    public int getRowCount() {
        return snippetVector.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    public Snippet getSnippetAt(int rowIndex) {
        try {
            return snippetVector.get(rowIndex);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        Snippet snippet = getSnippetAt(rowIndex);
        if (columnIndex == LANGUAGE) {
            return snippet.getLanguage().toString();
        }

        if (columnIndex == TITLE) {
            return snippet.getTitle();
        }
        return null;
    }

    public void updateWithSnippets(SnippetCollection snippets) {
        snippetVector.clear();
        for (Snippet snippet : snippets) {
            snippetVector.add(snippet);
        }
        fireTableDataChanged();
    }

    public void filter(String filterText) {
        // TODO use Operation
        snippetVector.clear();
        String filterLowerCase = filterText.toLowerCase();

        try {
            for (Snippet snippet : framework.getData().getSnippets()) {
                if (snippet.getTitle().toLowerCase().contains(filterLowerCase) ||
                        snippet.getCode().toLowerCase().contains(filterLowerCase)
                        || snippet.getDescription().toLowerCase().contains(filterLowerCase)) {
                    snippetVector.add(snippet);
                }
            }
        } catch (TagMyCodeJsonException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }
}
