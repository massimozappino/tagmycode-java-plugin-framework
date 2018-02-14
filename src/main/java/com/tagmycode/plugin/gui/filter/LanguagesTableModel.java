package com.tagmycode.plugin.gui.filter;

import com.tagmycode.plugin.filter.FilterLanguages;
import com.tagmycode.plugin.filter.LanguageFilterEntry;
import com.tagmycode.sdk.model.Language;

import javax.swing.table.AbstractTableModel;

public class LanguagesTableModel extends AbstractTableModel {
    private FilterLanguages filterLanguages = new FilterLanguages();

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return filterLanguages.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        LanguageFilterEntry languageIntegerEntry = filterLanguages.get(rowIndex);
        if (columnIndex == 0) {
            return languageIntegerEntry.getLanguage();
        }
        return languageIntegerEntry.getCount();
    }

    public void setFilterLanguages(FilterLanguages filterLanguages) {
        this.filterLanguages = filterLanguages;
    }

    public FilterLanguages getFilterLanguages() {
        return filterLanguages;
    }

    public int getLanguagePosition(Language language) {
        if (language == null) {
            return -1;
        }
        for (int i = 0; i < filterLanguages.size(); i++) {
            if (filterLanguages.get(i).getLanguage().equals(language)) {
                return i;
            }
        }
        return -1;
    }
}