package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.IconResources;
import com.tagmycode.plugin.gui.AbstractSnippetsListGui;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

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

        sortByCreationDate();

        cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureTableHeader();
    }

    private void sortByCreationDate() {
        DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
        ArrayList<RowSorter.SortKey> list = new ArrayList<RowSorter.SortKey>();
        list.add(new RowSorter.SortKey(SnippetsTableModel.CREATED, SortOrder.DESCENDING));
        sorter.setSortKeys(list);
        sorter.sort();
    }

    private void configureTableHeader() {
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new KeepSortIconHeaderRenderer(header.getDefaultRenderer()));
        header.setReorderingAllowed(false);

        TableColumn columnIsPrivate = table.getColumnModel().getColumn(SnippetsTableModel.IS_PRIVATE);
        columnIsPrivate.setHeaderValue(IconResources.createImageIcon("private.png"));
        columnIsPrivate.setHeaderRenderer(new IconTableHeaderCellRender(header.getDefaultRenderer()));
        columnIsPrivate.setMaxWidth(32);
        columnIsPrivate.setMinWidth(32);
        columnIsPrivate.setResizable(false);

        TableColumn columnLanguage = table.getColumnModel().getColumn(SnippetsTableModel.LANGUAGE);
        columnLanguage.setPreferredWidth(80);

        TableColumn columnTitle = table.getColumnModel().getColumn(SnippetsTableModel.TITLE);
        columnTitle.setPreferredWidth(200);
        columnTitle.setCellRenderer(new TitleSnippetTableCellRender());
        columnLanguage.setCellRenderer(new DefaultSnippetTableCellRender());
        columnIsPrivate.setCellRenderer(new PrivateSnippetTableCellRender());
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

