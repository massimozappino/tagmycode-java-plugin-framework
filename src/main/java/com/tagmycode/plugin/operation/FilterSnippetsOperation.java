package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;

public class FilterSnippetsOperation extends TagMyCodeAsynchronousOperation<Void> {
    private final Framework framework;
    private SnippetsTable snippetsTable;
    private String searchText = "";
    private Language filterLanguage = null;

    public FilterSnippetsOperation(SnippetsTable snippetsTable) {
        super(snippetsTable.getFramework());
        this.snippetsTable = snippetsTable;
        this.framework = snippetsTable.getFramework();
        // TODO method to reset filters
    }

    @Override
    protected Void performOperation() {
        final ArrayList<Integer> filteredIds = filterSnippets();

        final RowFilter<SnippetsTableModel, Integer> filter = new RowFilter<SnippetsTableModel, Integer>() {
            public boolean include(Entry entry) {
                int position = (int) entry.getIdentifier();
                return filteredIds.contains(position);
            }
        };

        final TableRowSorter<SnippetsTableModel> sorter = snippetsTable.sorter;

        doFilterInGuiThread(filter, sorter);

        return null;
    }

    private void doFilterInGuiThread(final RowFilter<SnippetsTableModel, Integer> filter, final TableRowSorter<SnippetsTableModel> sorter) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                // TODO test condition for each filter
                if (searchText.length() == 0 && filterLanguage == null) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(filter);
                }
            }
        });
    }

    protected ArrayList<Integer> filterSnippets() {
        final ArrayList<Integer> filteredIds = new ArrayList<>();
        int position = 0;
        for (Snippet snippet : framework.getData().getSnippets()) {
            if (filterFullText(snippet) && filterLanguage(snippet)) {
                filteredIds.add(position);
            }
            position++;
        }
        return filteredIds;
    }

    private boolean filterLanguage(Snippet snippet) {
        return filterLanguage == null || snippet.getLanguage().equals(filterLanguage);
    }

    private boolean filterFullText(Snippet snippet) {
        return search(searchText, snippet.getCode())
                || search(searchText, snippet.getTitle())
                || search(searchText, snippet.getDescription())
                || search(searchText, snippet.getTags());
    }

    protected boolean search(String query, String fieldValue) {
        fieldValue = fieldValue.toLowerCase();
        String[] tokens = query.split(" ");

        boolean ret = true;
        for (String token : tokens) {
            ret = fieldValue.contains(token) && ret;
        }

        return ret;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText.trim().toLowerCase();
    }

    public void setFilterLanguage(Language filterLanguage) {
        this.filterLanguage = filterLanguage;
    }
}

