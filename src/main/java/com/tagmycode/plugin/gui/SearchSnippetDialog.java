package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.operation.SearchSnippetOperation;
import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchSnippetDialog extends AbstractDialog {
    private JPanel contentPane;
    private JButton buttonOk;
    private JButton buttonCancel;
    private JTextField searchTextField;
    private SnippetEditorPane snippetEditorPane;
    private JList resultList;
    private JButton insertButton;
    private JLabel resultsFoundLabel;
    private DefaultListModel model;
    private IDocumentInsertText documentUpdate;
    private DefaultListCellRenderer defaultListCellRenderer;
    private DisabledItemSelectionModel disabledItemSelectionModel;
    private SnippetRenderer snippetRenderer;
    private DefaultListSelectionModel defaultListSelectionModel;

    public SearchSnippetDialog(IDocumentInsertText documentInsertText, final Framework framework, Frame parent) {
        super(framework, parent);
        this.documentUpdate = documentInsertText;

        defaultInitWindow();
        initWindow();
        initResultList();
        if (documentInsertText == null) {
            insertButton.setVisible(false);
        }
    }

    private void initResultList() {
        defaultListCellRenderer = new DefaultListCellRenderer();
        disabledItemSelectionModel = new DisabledItemSelectionModel();
        snippetRenderer = new SnippetRenderer();
        defaultListSelectionModel = new DefaultListSelectionModel();
        model = new DefaultListModel();
        resultList.setModel(model);
        resultList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Snippet snippet = getSelectedSnippet();
                    if (snippet != null) {
                        snippetEditorPane.setTextWithSnippet(snippet);
                        insertButton.setEnabled(true);
                    } else {
                        insertButton.setEnabled(false);
                    }
                }
            }
        });
        refreshResultsFoundLabel();
    }

    public Snippet getSelectedSnippet() {
        Snippet selectedSnippet = null;
        if (!resultList.isSelectionEmpty()) {
            selectedSnippet = (Snippet) model.getElementAt(resultList.getSelectedIndex());
        }

        return selectedSnippet;
    }

    private void insertText() {
        Snippet selectedSnippet = getSelectedSnippet();

        if (selectedSnippet != null) {
            String code = selectedSnippet.getCode();
            documentUpdate.insertText(code);
            closeDialog();
        }
    }

    @Override
    protected void initWindow() {
        getRootPane().setDefaultButton(null);
        searchTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        setSize(800, 400);
        setResizable(true);
        setTitle("Search snippets");
        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertText();
            }
        });
        snippetEditorPane.setEditable(false);
        insertButton.setEnabled(false);

    }

    private void refreshResultsFoundLabel() {
        int size;
        if (resultList.getSelectionModel() == disabledItemSelectionModel) {
            size = 0;
        } else {
            size = model.getSize();
        }
        resultsFoundLabel.setText(String.format("%d snippets found", size));
    }

    public DefaultListModel getModel() {
        return model;
    }

    @Override
    protected void onOK() {

    }

    private void search() {
        String query = getQuery();
        if (query.length() > 0) {
            clearResults();
            new SearchSnippetOperation(this, query).runWithTask(framework.getTaskFactory(), "Searching snippets");
        }
    }

    private void clearResults() {
        model.removeAllElements();
        snippetEditorPane.clear();
    }

    @Override
    public void closeDialog() {
        setVisible(false);
    }

    @Override
    public JButton getButtonOk() {
        return buttonOk;
    }

    @Override
    protected JButton getButtonCancel() {
        return buttonCancel;
    }

    @Override
    protected JPanel getContentPanePanel() {
        return contentPane;
    }

    public String getQuery() {
        return searchTextField.getText();
    }

    public void updateListWithSnippets(final ModelCollection<Snippet> snippets) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                clearResults();
                if (snippets.size() == 0) {
                    resultList.setCellRenderer(defaultListCellRenderer);
                    resultList.setSelectionModel(disabledItemSelectionModel);
                    model.addElement("No results found");
                } else {
                    resultList.setCellRenderer(snippetRenderer);
                    resultList.setSelectionModel(defaultListSelectionModel);

                    for (Snippet snippet : snippets) {
                        model.addElement(snippet);
                    }
                }
                refreshResultsFoundLabel();
            }
        });
    }

    @Override
    public void showAtCenter() {
        super.showAtCenter();
        searchTextField.selectAll();
        searchTextField.requestFocus();
    }

    public JTextField getSearchTextBox() {
        return searchTextField;
    }

    public JButton getSearchButton() {
        return buttonOk;
    }

    public JButton getInsertButton() {
        return insertButton;
    }

    public JList getResultList() {
        return resultList;
    }

    public JLabel getResultsFoundLabel() {
        return resultsFoundLabel;
    }
}

class DisabledItemSelectionModel extends DefaultListSelectionModel {

    @Override
    public void setSelectionInterval(int index0, int index1) {
        super.setSelectionInterval(-1, -1);
    }
}
