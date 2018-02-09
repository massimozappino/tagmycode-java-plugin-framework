package com.tagmycode.plugin.gui;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.*;

public class FilterLanguagesPanel {
    private JPanel jpanel;
    private Data data;
    private FilterSnippetsOperation filterSnippetsOperation;
    private Language selectedLanguage;
    private final LanguagesTableModel model;
    private final JTable table;

    public FilterLanguagesPanel(FilterSnippetsOperation filterSnippetsOperation, Data data, JPanel jpanel) {
        this.filterSnippetsOperation = filterSnippetsOperation;
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        this.data = data;
        this.jpanel = jpanel;
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

        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void filterLanguage() {
        int selectedRow = table.getSelectedRow();
        int ok = table.convertRowIndexToModel(selectedRow);
        Language language = (Language) model.getValueAt(ok, 0);
        selectedLanguage = language;
        filterSnippetsOperation.setFilterLanguage(language);
        filterSnippetsOperation.stop();
        filterSnippetsOperation.start();
    }

    public void refresh() {
        HashMap<Language, Integer> mp = fetchLanguages();
        model.setData(mp);
        model.fireTableDataChanged();
    }

    private HashMap<Language, Integer> fetchLanguages() {
        DbService dbService = data.getStorageEngine().getDbService();
        Dao<Snippet, String> snippetDao = dbService.snippetDao();

        GenericRawResults<String[]> query;
        HashMap<Language, Integer> map = new LinkedHashMap<>();
        try {
            query = snippetDao.queryRaw(
                    "select languages.code, count(*) as X from languages inner join snippets on snippets.language_id = languages.id  group by name order by X DESC");
            LanguagesCollection languages = data.getLanguages();
            for (String[] language : query) {
                map.put(languages.findByCode(language[0]), Integer.valueOf(language[1]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}

class LanguagesTableModel extends AbstractTableModel {
    private ArrayList<Map.Entry<Language, Integer>> list = new ArrayList<>();

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return list.size();
    }

    public Object getValueAt(int r, int c) {
        Map.Entry<Language, Integer> languageIntegerEntry = list.get(r);
        if (c == 0) {
            return languageIntegerEntry.getKey();
        }
        return languageIntegerEntry.getValue();
    }

    public void setData(HashMap<Language, Integer> data) {
        ArrayList<Map.Entry<Language, Integer>> list = new ArrayList<>();
        for (Object o : data.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            list.add(pair);
        }
        this.list = list;
    }
}