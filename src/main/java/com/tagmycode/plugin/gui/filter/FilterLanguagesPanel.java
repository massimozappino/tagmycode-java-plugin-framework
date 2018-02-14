package com.tagmycode.plugin.gui.filter;

import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.FilterLanguagesLoader;
import com.tagmycode.plugin.filter.FilterLanguages;
import com.tagmycode.plugin.gui.GuiUtil;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;
import com.tagmycode.sdk.model.Language;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FilterLanguagesPanel {
    private FilterSnippetsOperation filterSnippetsOperation;
    private final LanguagesTableModel model;
    private final JTable table;
    private FilterLanguagesLoader filterLanguagesLoader;

    public FilterLanguagesPanel(FilterSnippetsOperation filterSnippetsOperation, Data data, JPanel jpanel) {
        this.filterSnippetsOperation = filterSnippetsOperation;
        filterLanguagesLoader = new FilterLanguagesLoader(data);
        model = new LanguagesTableModel();
        table = new JTable(model) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex,
                                        boolean toggle, boolean extend) {
                super.changeSelection(rowIndex, columnIndex, true, false);
            }
        };

        table.getColumnModel().getColumn(1).setPreferredWidth(27);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    filterLanguage();
                }
            }
        });
        JScrollPane jScrollPane = new JScrollPane(table);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jpanel.add(jScrollPane);
        TableRowSorter<LanguagesTableModel> sorter = new TableRowSorter<>(model);

        table.setRowSorter(sorter);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setShowGrid(false);
        table.setBackground(null);
        GuiUtil.removeBorder(jScrollPane);
        table.setTableHeader(null);
        table.setDefaultRenderer(Object.class, new LanguageTableRenderer());

        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        removeDefaultStrokes();
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
        filterSnippetsOperation.setFilterLanguage(language);
        filterSnippetsOperation.stop();
        filterSnippetsOperation.start();
    }

    public void refresh() {
        FilterLanguages mp = filterLanguagesLoader.load();
        model.setFilterLanguages(mp);

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

class LanguageTableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(noFocusBorder);
        return this;
    }

}