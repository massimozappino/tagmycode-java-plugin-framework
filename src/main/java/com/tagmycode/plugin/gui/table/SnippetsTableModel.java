package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.Data;
import com.tagmycode.sdk.model.DefaultSnippet;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.table.AbstractTableModel;
import java.util.Date;

public class SnippetsTableModel extends AbstractTableModel {
    public static final int IS_PRIVATE = 0;
    public final static int TITLE = 1;
    public final static int LANGUAGE = 2;
    public static final int MODIFIED = 3;
    private final Data data;

    private String[] columns;

    public SnippetsTableModel(Data data) {
        this.data = data;
        columns = new String[]{"Private", "Title", "Language", "Modified"};
    }

    @Override
    public int getRowCount() {
        return data.getSnippets().size();
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
            return data.getSnippets().get(rowIndex);
        } catch (ArrayIndexOutOfBoundsException e) {
            // TODO should return null
            return new DefaultSnippet();
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
            case MODIFIED:
                return snippet.getUpdateDate();
        }
        return null;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case IS_PRIVATE:
                return Boolean.class;
            case MODIFIED:
                return Date.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public void fireSnippetsChanged() {
        fireTableDataChanged();
    }

}
