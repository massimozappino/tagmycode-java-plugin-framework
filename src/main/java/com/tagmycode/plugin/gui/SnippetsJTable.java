package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.*;
import java.awt.*;

public class SnippetsJTable extends AbstractSnippetsListGui {

    private final JScrollPane scrollPane;
    private JTable table;
    private SnippetsJTableModel tableModel;
    private ListSelectionModel cellSelectionModel;


    public SnippetsJTable() {
        tableModel = new SnippetsJTableModel();
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);

        cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!e.getValueIsAdjusting()) {
//                    Snippet snippet = getSelectedSnippet();
//                    System.out.println("Selected: " + snippet.getTitle());
//                }
//            }
//
//        });

    }

    public ListSelectionModel getCellSelectionModel() {
        return cellSelectionModel;
    }

    @Override
    public JComponent getMainComponent() {
        return scrollPane;
    }

    @Override
    public void updateWithSnippets(SnippetCollection snippets) {
        tableModel.updateWithSnippets(snippets);
    }

    @Override
    public Snippet getSelectedSnippet() {
        return tableModel.getSnippetAt(table.getSelectedRow());
    }

    @Override
    public JComponent getComponent() {
        return table;
    }
}
