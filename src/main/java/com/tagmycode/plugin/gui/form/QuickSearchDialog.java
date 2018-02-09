package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.FilterSnippetsTextField;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.MoveUpDownFilterFieldKeyListener;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;

public class QuickSearchDialog extends Windowable {
    private final JButton buttonOk;
    private final JPanel previewPanel;
    private JButton buttonCancel;
    private JTable jTable;
    private FilterSnippetsTextField filterSnippetsTextField;
    private JPanel mainPanel;
    private JPanel resultsPanel;
    private JPanel snippetViewPanel;
    private JPanel centerPanel;
    private JButton insertAtCursorButton;
    private JButton openButton;
    private JPanel filterPanel;
    private IDocumentInsertText documentInsertText;
    private SnippetsTable snippetsTable;
    private SnippetView snippetView;

    public QuickSearchDialog(final Framework framework, Frame parent) {
        super(framework, parent);
        snippetView = new SnippetView(framework.getSyntaxSnippetEditorFactory());
        snippetsTable = new SnippetsTable(framework);

        buttonOk = new JButton();
        insertAtCursorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertCodeIntoDocument();
            }
        });
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSnippet();
            }
        });

        disableSnippetButtons();

        configureJTable();

        snippetViewPanel.setPreferredSize(new Dimension(-1, 130));

        previewPanel = new JPanel(new GridBagLayout());
        previewPanel.add(new JLabel("Select a snippet to preview"));
        snippetViewPanel.removeAll();
        snippetViewPanel.add(previewPanel);

        filterSnippetsTextField = new FilterSnippetsTextField(snippetsTable.getFilterSnippetsOperation());
        filterSnippetsTextField.addKeyListener(new MoveUpDownFilterFieldKeyListener(jTable));
        filterSnippetsTextField.addKeyListener(createInsertIntoDocumentKeyListener());
        filterPanel.add(filterSnippetsTextField);

        snippetsTable.getCellSelectionModel().addListSelectionListener(createSelectionListener());

        resultsPanel.add(snippetsTable.getMainComponent());

        defaultInitWindow();
        initWindow();
    }

    private void configureJTable() {
        jTable = snippetsTable.getJTable();
        jTable.setTableHeader(null);
        TableColumnModel columnModel = jTable.getColumnModel();
        jTable.removeColumn(columnModel.getColumn(SnippetsTableModel.MODIFIED));
        jTable.removeColumn(columnModel.getColumn(SnippetsTableModel.IS_PRIVATE));
        jTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSnippet();
                }
            }
        });
        jTable.addKeyListener(new KeyAdapter() {
                                  @Override
                                  public void keyPressed(KeyEvent e) {
                                      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                          insertCodeIntoDocument();
                                      }
                                  }
                              }
        );
    }

    private void editSnippet() {
        framework.showSnippetDialog(snippetsTable.getSelectedSnippet());
        hideDialog();
    }

    private ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    snippetViewPanel.removeAll();
                    disableSnippetButtons();

                    Snippet snippet = snippetsTable.getSelectedSnippet();

                    if (snippet != null) {
                        snippetView.setSnippet(snippet);
                        SyntaxSnippetEditor snippetEditorPane = snippetView.getSnippetEditorPane();
                        snippetEditorPane.setPreview();
                        snippetViewPanel.add(snippetEditorPane.getMainComponent());
                        enableSnippetButtons();
                    } else {
                        snippetViewPanel.add(previewPanel);
                    }
                    snippetViewPanel.revalidate();
                    snippetViewPanel.repaint();
                }
            }
        };
    }

    private void disableSnippetButtons() {
        openButton.setEnabled(false);
        insertAtCursorButton.setEnabled(false);
    }

    private void enableSnippetButtons() {
        openButton.setEnabled(true);
        insertAtCursorButton.setEnabled(true);
    }

    private KeyListener createInsertIntoDocumentKeyListener() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    insertCodeIntoDocument();
                    hideDialog();
                }
            }
        };
    }

    private void insertCodeIntoDocument() {
        if (documentInsertText != null) {
            Snippet snippet = getSelectedSnippet();
            if (snippet != null) {
                documentInsertText.insertText(snippet.getCode());
            }
            hideDialog();
        }
    }

    public Snippet getSelectedSnippet() {
        return snippetsTable.getSelectedSnippet();
    }

    @Override
    protected void initWindow() {
        setTitle("Quick search");
        getRootPane().setDefaultButton(null);
        setSize(400, 300);
        setResizable(true);
        hideOnFocusLost();
    }


    @Override
    protected void onOK() {
    }

    @Override
    protected void onCancel() {
        hideDialog();
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
        return mainPanel;
    }

    public void setDocumentInsertText(IDocumentInsertText documentInsertText) {
        this.documentInsertText = documentInsertText;
        if (documentInsertText == null) {
            insertAtCursorButton.setVisible(false);
        } else {
            insertAtCursorButton.setVisible(true);
        }
    }

    @Override
    public void display() {
        super.display();
        filterSnippetsTextField.selectAll();
        filterSnippetsTextField.requestFocus();
        filterSnippetsTextField.doFilter();
    }
}