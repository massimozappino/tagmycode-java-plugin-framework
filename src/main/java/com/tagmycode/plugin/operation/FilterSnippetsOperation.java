package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.sql.SQLException;
import java.util.Vector;

public class FilterSnippetsOperation extends TagMyCodeAsynchronousOperation<Void> {
    private final Framework framework;
    private String filterText;
    private SnippetsTable snippetsTable;

    public FilterSnippetsOperation(Framework framework, SnippetsTable snippetsTable, String filterText) {
        super(framework);
        this.snippetsTable = snippetsTable;
        this.filterText = filterText.trim().toLowerCase();
        this.framework = framework;
    }

    @Override
    protected Void performOperation() throws Exception {
        final Vector<Integer> filteredIds = filterSnippets();

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
                if (filterText.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(filter);
                }
            }
        });
    }

    private Vector<Integer> filterSnippets() throws SQLException {
        final Vector<Integer> filteredIds = new Vector<>();
        int position = 0;
        for (Snippet snippet : framework.getData().getSnippets()) {
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

