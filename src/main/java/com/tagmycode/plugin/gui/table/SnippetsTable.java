package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.gui.AbstractSnippetsListGui;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.*;
import java.awt.*;

public class SnippetsTable extends AbstractSnippetsListGui {

    private final JScrollPane scrollPane;
    private JTable table;
    private SnippetsTableModel tableModel;
    private ListSelectionModel cellSelectionModel;

    public SnippetsTable() {
        tableModel = new SnippetsTableModel();
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        scrollPane = new JScrollPane(table);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);

        cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(SnippetsTableModel.TITLE).setCellRenderer(new TitleSnippetTableCellRender());
        table.getColumnModel().getColumn(SnippetsTableModel.LANGUAGE).setCellRenderer(new DefaultSnippetTableCellRender());
        table.getColumnModel().getColumn(SnippetsTableModel.IS_PRIVATE).setCellRenderer(new PrivateSnippetTableCellRender());
        table.getColumnModel().getColumn(SnippetsTableModel.CREATED).setCellRenderer(new DateSnippetTableCellRender());
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
        return tableModel.getSnippetAt(getSelectedModelIndex());
    }


    private int getSelectedModelIndex() {
        // TODO manage IndexOutOfBoundsException
        try {
            int selectedRow = table.getSelectedRow();
            int i = table.convertRowIndexToModel(selectedRow);
            return i;
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    public JTable getSnippetsComponent() {
        return table;
    }

    public void addSnippet(Snippet snippet) {
        tableModel.addSnippet(snippet);
    }
}

