package com.tagmycode.plugin.gui.table;

import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.Vector;

public class SnippetsTableModel extends AbstractTableModel {
    public final static int TITLE = 0;
    public final static int LANGUAGE = 1;
    public static final int IS_PRIVATE = 2;
    public static final int CREATED = 3;

    private Vector<Snippet> snippetVector;
    private String[] columns;

    public SnippetsTableModel() {
        snippetVector = new Vector<Snippet>();
        columns = new String[]{"Title", "Language", "Private", "Created"};
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Snippet snippet = getSnippetAt(rowIndex);
        switch (columnIndex) {
            case LANGUAGE:
                return snippet.getLanguage().toString();
            case TITLE:
                return snippet.getTitle();
            case IS_PRIVATE:
                return snippet.isPrivate();
            case CREATED:
                return snippet.getCreationDate();
        }
        return null;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case IS_PRIVATE:
                return Boolean.class;
            case CREATED:
                return Date.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public void updateWithSnippets(SnippetCollection snippets) {
        snippetVector.clear();
        for (Snippet snippet : snippets) {
            snippetVector.add(snippet);
        }
        fireTableDataChanged();
    }

    public void addSnippet(Snippet snippet) {
        snippetVector.add(snippet);
        fireTableDataChanged();
    }
}
