package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.FilterLanguagesLoader;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.GuiUtil;
import com.tagmycode.plugin.gui.filter.LanguagesTableModel;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;
import com.tagmycode.sdk.model.Language;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FiltersPanelForm extends AbstractGui {
    private JPanel languagesPanel;
    private JSplitPane mainComponent;
    private JPanel emptyPanel;
    private FilterSnippetsOperation filterSnippetsOperation;
    private final LanguagesTableModel model;
    private JTable table;
    private FilterLanguagesLoader filterLanguagesLoader;


    public FiltersPanelForm(FilterSnippetsOperation filterSnippetsOperation, Data data) {
        this.filterSnippetsOperation = filterSnippetsOperation;
        filterLanguagesLoader = new FilterLanguagesLoader(data);
        model = new LanguagesTableModel();
        JScrollPane languagesScrollPane = configureTable();

        languagesPanel.add(languagesScrollPane, BorderLayout.CENTER);

        removeDefaultStrokes();
    }

    private JScrollPane configureTable() {
        table = new JTable(model) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex,
                                        boolean toggle, boolean extend) {
                super.changeSelection(rowIndex, columnIndex, true, false);
            }
        };
        table.getColumnModel().getColumn(1).setMaxWidth(35);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    filterLanguage();
                }
            }
        });
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        JScrollPane languagesScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        GuiUtil.removeBorder(languagesScrollPane);

        TableRowSorter<LanguagesTableModel> sorter = new TableRowSorter<>(model);

        table.setRowSorter(sorter);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setShowGrid(false);
        table.setBackground(null);
        table.setTableHeader(null);
        table.setDefaultRenderer(Object.class, new com.tagmycode.plugin.gui.filter.LanguageTableRenderer());

        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return languagesScrollPane;
    }

    @Override
    public JComponent getMainComponent() {
        return mainComponent;
    }

    private void removeDefaultStrokes() {
        GuiUtil.removeKeyStrokeAction(table, KeyEvent.VK_ENTER);
        GuiUtil.removeKeyStrokeAction(table, KeyEvent.VK_TAB);
        GuiUtil.removeKeyStrokeAction(table, KeyEvent.VK_UP);
        GuiUtil.removeKeyStrokeAction(table, KeyEvent.VK_DOWN);
        GuiUtil.removeKeyStrokeAction(table, KeyEvent.VK_U);
        GuiUtil.removeKeyStrokeAction(table, KeyEvent.VK_DOWN);
    }

    private void filterLanguage() {
        int selectedRow = table.getSelectedRow();
        Language language = null;
        if (selectedRow != -1) {
            int ok = table.convertRowIndexToModel(selectedRow);
            language = (Language) model.getValueAt(ok, 0);
        }
        filterSnippetsOperation.getSnippetsTable().getJTable().clearSelection();

        filterSnippetsOperation.setFilterLanguage(language);
        filterSnippetsOperation.filter();
    }

    public void refresh() {
        model.setFilterLanguages(filterLanguagesLoader.load());

        Language filterLanguage = filterSnippetsOperation.getFilterLanguage();
        model.fireTableDataChanged();

        selectLanguageOnTable(filterLanguage);
    }

    private void selectLanguageOnTable(Language language) {
        LanguagesTableModel model = (LanguagesTableModel) table.getModel();
        int languageModelPosition = model.getLanguagePosition(language);
        if (languageModelPosition >= 0) {
            int viewRowIndex = table.convertRowIndexToView(languageModelPosition);
            table.setRowSelectionInterval(viewRowIndex, viewRowIndex);
        }
    }
}
