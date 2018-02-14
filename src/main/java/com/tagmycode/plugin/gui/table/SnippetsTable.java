package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.IconResources;
import com.tagmycode.plugin.TableModelSnippetNotFoundException;
import com.tagmycode.plugin.gui.AbstractSnippetsListGui;
import com.tagmycode.plugin.gui.GuiUtil;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;
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
    private final Framework framework;
    private JTable jTable;
    private SnippetsTableModel model;
    private ListSelectionModel cellSelectionModel;
    private FilterSnippetsOperation filterSnippetsOperation;

    public SnippetsTable(Framework framework) {
        this.framework = framework;
        model = new SnippetsTableModel(framework.getData());
        jTable = new JTable(model);
        sorter = new TableRowSorter<>(model);
        jTable.setRowSorter(sorter);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable.setIntercellSpacing(new Dimension(0, 0));
        jTable.setCellSelectionEnabled(false);
        jTable.setRowSelectionAllowed(true);
        jTable.setShowGrid(false);
        scrollPane = new JScrollPane(jTable);
        GuiUtil.removeBorder(scrollPane);

        filterSnippetsOperation = new FilterSnippetsOperation(this);
        cellSelectionModel = jTable.getSelectionModel();
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
        JTableHeader header = jTable.getTableHeader();
        header.setDefaultRenderer(new KeepSortIconHeaderRenderer(header.getDefaultRenderer()));
        header.setReorderingAllowed(false);

        TableColumn columnIsPrivate = jTable.getColumnModel().getColumn(SnippetsTableModel.IS_PRIVATE);
        columnIsPrivate.setHeaderValue(IconResources.createImageIcon("private.png"));
        columnIsPrivate.setHeaderRenderer(new IconTableHeaderCellRender(header.getDefaultRenderer()));
        columnIsPrivate.setMaxWidth(32);
        columnIsPrivate.setMinWidth(32);
        columnIsPrivate.setResizable(false);

        TableColumn columnLanguage = jTable.getColumnModel().getColumn(SnippetsTableModel.LANGUAGE);
        columnLanguage.setPreferredWidth(80);

        TableColumn columnTitle = jTable.getColumnModel().getColumn(SnippetsTableModel.TITLE);
        columnTitle.setPreferredWidth(200);
        columnTitle.setCellRenderer(new TitleSnippetTableCellRender());
        columnLanguage.setCellRenderer(new DefaultSnippetTableCellRender());
        columnIsPrivate.setCellRenderer(new PrivateSnippetTableCellRender());
        jTable.getColumnModel().getColumn(SnippetsTableModel.MODIFIED).setCellRenderer(new DateSnippetTableCellRender());
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
        filterSnippetsOperation.filter();
        Snippet selectedSnippet = getSelectedSnippet();
        model.fireSnippetsChanged();
        if (selectedSnippet != null) {
            int positionOfSnippet = getSnippetViewIndex(selectedSnippet);
            jTable.setRowSelectionInterval(positionOfSnippet, positionOfSnippet);
        }
    }

    private int getSnippetViewIndex(Snippet selectedSnippet) {
        int positionOfSnippet = model.getPositionOfSnippet(selectedSnippet);
        return jTable.convertRowIndexToView(positionOfSnippet);
    }

    @Override
    public Snippet getSelectedSnippet() {
        try {
            return model.getSnippetAt(getSelectedModelIndex());
        } catch (TableModelSnippetNotFoundException e) {
            return null;
        }
    }

    private int getSelectedModelIndex() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow >= 0) {
            return jTable.convertRowIndexToModel(selectedRow);
        } else {
            return -1;
        }
    }

    @Override
    public JTable getJTable() {
        return jTable;
    }

    public Framework getFramework() {
        return framework;
    }

    public FilterSnippetsOperation getFilterSnippetsOperation() {
        return filterSnippetsOperation;
    }
}

