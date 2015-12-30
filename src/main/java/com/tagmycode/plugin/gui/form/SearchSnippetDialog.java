package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.SnippetEditorPane;
import com.tagmycode.plugin.gui.SnippetsJList;
import com.tagmycode.plugin.gui.operation.SearchSnippetsOperation;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

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
    private JPanel leftPane;
    private JSplitPane splitPane;
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
        snippetsList.getComponent().addListSelectionListener(new ListSelectionListener() {
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

        getDialog().getRootPane().setDefaultButton(null);
        searchTextField.addActionListener(searchActionListener);
        getDialog().setSize(800, 400);
        getDialog().setResizable(true);
        getDialog().setTitle("Search snippets");
        buttonOk.addActionListener(searchActionListener);
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertText();
            }
        });
        snippetEditorPane.setEditable(false);
        insertButton.setEnabled(false);
        splitPane.setDividerLocation(200);
        snippetsList = new SnippetsJList();
        leftPane.add(snippetsList.getMainComponent(), BorderLayout.CENTER);
    }

    private void refreshResultsFoundLabel() {
        int size = snippetsList.getSnippetsSize();
        resultsFoundLabel.setText(String.format("%d snippets found", size));
    }

    public DefaultListModel<Snippet> getModel() {
        return snippetsList.getSnippetsModel();
    }

    @Override
    protected void onOK() {

    }

    private void search() {
        String query = getQuery();
        if (query.length() > 0) {
            clearResults();
            new SearchSnippetsOperation(this, query).runWithTask(framework.getTaskFactory(), "Searching snippets");
        }
    }

    private void clearResults() {
        setResultsFoundLabelEmpty();
        snippetsList.clearAll();
        snippetEditorPane.clear();
    }

    @Override
    public void closeDialog() {
        getDialog().setVisible(false);
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
    public JComponent getMainComponent() {
        return contentPane;
    }

    public String getQuery() {
        return searchTextField.getText();
    }

    public void updateListWithSnippets(final SnippetCollection snippets) {
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
    public void display() {
        super.display();
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

