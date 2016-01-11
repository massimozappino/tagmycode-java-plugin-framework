package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.IconResources;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SnippetsJTable extends AbstractSnippetsListGui {

    private final JScrollPane scrollPane;
    private JTable table;
    private SnippetsJTableModel tableModel;
    private ListSelectionModel cellSelectionModel;

    public SnippetsJTable(Framework framework) {
        tableModel = new SnippetsJTableModel(framework);
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        table.getColumnModel().getColumn(0).setCellRenderer(new SnippetTableCellRender());

        cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        return table.convertRowIndexToModel(table.getSelectedRow());
    }

    @Override
    public JTable getSnippetsComponent() {
        return table;
    }

    public void addSnippet(Snippet snippet) {
        tableModel.addSnippet(snippet);
    }
}

class SnippetTableCellRender extends DefaultTableCellRenderer {
    JLabel label = new JLabel();
    ImageIcon icon = IconResources.createImageIcon("snippet.png");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        label.setText((String) value);
        label.setIcon(icon);
        label.setOpaque(true);
        label.setBackground(tableCellRendererComponent.getBackground());
        label.setForeground(tableCellRendererComponent.getForeground());

        return label;
    }
}
