package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.Vector;

public class FilterSnippetsOperation extends TagMyCodeAsynchronousOperation<Void> {
    private SnippetsTab snippetsTab;
    private String filterText;

    public FilterSnippetsOperation(SnippetsTab snippetsTab, String filterText) {
        super(snippetsTab);
        this.snippetsTab = snippetsTab;
        this.filterText = filterText.trim().toLowerCase();
    }

    @Override
    protected Void performOperation() throws Exception {
        final Vector<Integer> filteredIds = filterSnippets();

        RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
            public boolean include(Entry entry) {
                int position = Integer.parseInt(entry.getIdentifier().toString());
                return filteredIds.contains(position);
            }
        };

        TableRowSorter<SnippetsTableModel> sorter = snippetsTab.getSnippetsTable().sorter;

        if (filterText.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(filter);
        }
        return null;
    }

    private Vector<Integer> filterSnippets() {
        final Vector<Integer> filteredIds = new Vector<Integer>();
        int position = 0;
        for (Snippet snippet : snippetsTab.getFramework().getData().getSnippets()) {
            if (search(filterText, snippet.getCode())
                    || search(filterText, snippet.getTitle())
                    || search(filterText, snippet.getDescription())
                    || search(filterText, snippet.getTags())
                    ) {
                filteredIds.add(position);
            }
            position++;
        }
        return filteredIds;
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
}

