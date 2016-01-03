package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class SnippetsJTableModel extends AbstractTableModel {
    public final static int LANGUAGE = 0;
    public final static int TITLE = 1;

    private Vector<Snippet> snippetVector;
    private String[] columns;

    public SnippetsJTableModel() {
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
        return snippetVector.get(rowIndex);
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
}
