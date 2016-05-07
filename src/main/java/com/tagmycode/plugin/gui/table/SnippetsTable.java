package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.IconResources;
import com.tagmycode.plugin.gui.AbstractSnippetsListGui;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;

public class SnippetsTable extends AbstractSnippetsListGui {

    public final TableRowSorter<SnippetsTableModel> sorter;
    private final JScrollPane scrollPane;
    private JTable table;
    private SnippetsTableModel model;
    private ListSelectionModel cellSelectionModel;

    public SnippetsTable(Framework framework) {
        model = new SnippetsTableModel(framework.getData());
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setShowGrid(false);

        cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureTableHeader();

        sortByColumn(SnippetsTableModel.MODIFIED);
    }

    private void sortByColumn(int columnIndexToSort) {
        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));

        sorter.setSortKeys(sortKeys);
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
        table.getColumnModel().getColumn(SnippetsTableModel.MODIFIED).setCellRenderer(new DateSnippetTableCellRender());
    }

    public ListSelectionModel getCellSelectionModel() {
        return cellSelectionModel;
    }

    @Override
    public JComponent getMainComponent() {
        return scrollPane;
    }

    @Override
    public void fireSnippetsChanged() {
        model.fireSnippetsChanged();
    }

    @Override
    public Snippet getSelectedSnippet() {
        return model.getSnippetAt(getSelectedModelIndex());
    }

    private int getSelectedModelIndex() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            return table.convertRowIndexToModel(selectedRow);
        } else {
            return -1;
        }
    }

    @Override
    public JTable getSnippetsComponent() {
        return table;
    }

}

