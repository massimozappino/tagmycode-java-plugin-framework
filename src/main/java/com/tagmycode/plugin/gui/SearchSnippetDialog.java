package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.operation.SearchSnippetOperation;
import com.tagmycode.sdk.model.Language;
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
    private JButton insertButton;
    private JLabel resultsFoundLabel;
    private SnippetsJList snippetsList;
    private JScrollPane listPane;
    private IDocumentInsertText documentUpdate;

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
        snippetsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Snippet snippet = snippetsList.getSelectedSnippet();
                    if (snippet != null) {
                        snippetEditorPane.setTextWithSnippet(snippet);
                        insertButton.setEnabled(true);
                    } else {
                        insertButton.setEnabled(false);
                    }
                }
            }
        });
        setResultsFoundLabelEmpty();
    }

    private void setResultsFoundLabelEmpty() {
        getResultsFoundLabel().setText("");
    }

    private void insertText() {
        Snippet selectedSnippet = snippetsList.getSelectedSnippet();

        if (selectedSnippet != null) {
            String code = selectedSnippet.getCode();
            documentUpdate.insertText(code);
            closeDialog();
        }
    }

    @Override
    protected void initWindow() {
        ActionListener searchActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        };

        getRootPane().setDefaultButton(null);
        searchTextField.addActionListener(searchActionListener);
        setSize(800, 400);
        setResizable(true);
        setTitle("Search snippets");
        buttonOk.addActionListener(searchActionListener);
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
        int size = snippetsList.getSnippetsSize();
        resultsFoundLabel.setText(String.format("%d snippets found", size));
    }

    public DefaultListModel getModel() {
        return snippetsList.getSnippetsModel();
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
        setResultsFoundLabelEmpty();
        snippetsList.clearAll();
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
                snippetsList.updateWithSnippets(snippets);
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

    public SnippetsJList getSnippetsList() {
        return snippetsList;
    }

    public JLabel getResultsFoundLabel() {
        return resultsFoundLabel;
    }

    private void createUIComponents() {
        snippetsList = new SnippetsJList();
    }
}

class DisabledItemSelectionModel extends DefaultListSelectionModel {

    @Override
    public void setSelectionInterval(int index0, int index1) {
        super.setSelectionInterval(-1, -1);
    }
}
