package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.FilterSnippetsTextField;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;

public class QuickSearchDialog extends AbstractDialog {
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
    private IDocumentInsertText documentInsertText;
    private SnippetsTable snippetsTable;

    public QuickSearchDialog(final Framework framework, Frame parent) {
        super(framework, parent);

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

        KeyListener insertCodeKeyListener = createInsertIntoDocumentKeyListener();
        filterSnippetsTextField.addKeyListener(insertCodeKeyListener);

        configureJTable();

        snippetViewPanel = new JPanel(new BorderLayout());
        snippetViewPanel.setPreferredSize(new Dimension(-1, 130));
        centerPanel.add(snippetViewPanel, BorderLayout.SOUTH);

        previewPanel = new JPanel(new GridBagLayout());
        previewPanel.add(new JLabel("Preview..."));
        snippetViewPanel.removeAll();
        snippetViewPanel.add(previewPanel);

        snippetsTable.getCellSelectionModel().addListSelectionListener(createSelectionListener());

        resultsPanel.add(snippetsTable.getMainComponent());

        defaultInitWindow();
        initWindow();
    }

    private void configureJTable() {
        jTable = snippetsTable.getSnippetsComponent();
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
                                      super.keyPressed(e);
                                      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                          insertCodeIntoDocument();
                                      }
                                  }
                              }
        );
    }

    private void editSnippet() {
        framework.showEditSnippetDialog(snippetsTable.getSelectedSnippet());
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
                    // TODO test
                    // TODO remove duplication from SnippetsTab#createSelectionListener
                    if (snippet != null) {
                        JComponent snippetViewForm = new SnippetView(snippet).getSnippetEditorPane().getMainComponent();
                        snippetViewPanel.add(snippetViewForm);
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
            documentInsertText.insertText(snippet.getCode());
            hideDialog();
        }
    }

    public Snippet getSelectedSnippet() {
        return snippetsTable.getSelectedSnippet();
    }

    @Override
    protected void initWindow() {
        getDialog().getRootPane().setDefaultButton(null);
        getDialog().setSize(400, 400);
        getDialog().setResizable(true);
        getDialog().setUndecorated(true);
        getDialog().setModal(false);
        getDialog().addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) {
                hideDialog();
            }

            public void windowGainedFocus(WindowEvent e) {
            }
        });
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

    private void createUIComponents() {
        snippetsTable = new SnippetsTable(framework);
        filterSnippetsTextField = new FilterSnippetsTextField(framework, getSnippetsTable());
        filterSnippetsTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_UP: {
                        cycleTableSelectionRows("up");
                        break;
                    }

                    case KeyEvent.VK_DOWN: {
                        cycleTableSelectionRows("down");
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void cycleTableSelectionRows(String direction) {
        int size = jTable.getRowCount();
        if (size > 0) {
            int selectedIndex = jTable.getSelectedRow();
            int newSelectionIndex = 0;

            if (direction.equals("up")) {
                newSelectionIndex = size - 1;
                if (selectedIndex > 0) {
                    newSelectionIndex = selectedIndex - 1;
                }
            } else {
                if (selectedIndex != size - 1) {
                    newSelectionIndex = selectedIndex + 1;
                }
            }
            selectTableRow(newSelectionIndex);
        }
    }

    private void selectTableRow(int newSelectionIndex) {
        jTable.setRowSelectionInterval(newSelectionIndex, newSelectionIndex);
        jTable.scrollRectToVisible(new Rectangle(jTable.getCellRect(newSelectionIndex, 0, true)));
    }

    @Override
    public void display() {
        super.display();
        filterSnippetsTextField.selectAll();
        filterSnippetsTextField.requestFocus();
        filterSnippetsTextField.doFilter();
    }

    public SnippetsTable getSnippetsTable() {
        return snippetsTable;
    }
}