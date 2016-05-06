package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.ClipboardCopy;
import com.tagmycode.plugin.gui.FilterSnippetsTextField;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.plugin.operation.DeleteSnippetOperation;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;

public class SnippetsTab extends AbstractGui implements IOnErrorCallback {
    protected JButton editSnippetButton;
    protected JButton deleteSnippetButton;
    protected JButton copyButton;
    protected JButton openInBrowser;
    private SnippetsTable snippetsTable;
    private JPanel snippetViewFormPane;
    private JButton newSnippetButton;
    private JPanel mainPanel;
    private JButton refreshButton;
    private JPanel leftPane;
    private FilterSnippetsTextField filterTextField;
    private JButton settingsButton;
    private JPanel snippetListPane;
    private Framework framework;
    private JTable jTable;
    private ClipboardCopy clipboardCopy = new ClipboardCopy();
    private int selectedRow;
    private Snippet selectedSnippet = null;
    private SnippetsTableModel model;

    public SnippetsTab(final Framework framework) {
        this.framework = framework;
        reset();
        initSnippetsJTable();

        leftPane.add(snippetsTable.getMainComponent(), BorderLayout.CENTER);

        initToolBarButtons(framework);
        initPopupMenuForJTextComponents(getMainComponent());
    }

    private void initToolBarButtons(final Framework framework) {
        editSnippetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSnippetAction();
            }
        });
        deleteSnippetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSnippetAction();
            }
        });
        newSnippetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSnippetAction(framework);
            }
        });
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCodeAction();
            }
        });
        openInBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSnippetInBrowser();
            }
        });
        refreshButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.syncSnippets();
            }
        });
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.showSettingsDialog();
            }
        });

        disableButtonsForSnippet();
    }

    private void initSnippetsJTable() {
        snippetsTable = new SnippetsTable(framework);

        jTable = snippetsTable.getSnippetsComponent();
        model = (SnippetsTableModel) jTable.getModel();
        snippetsTable.getCellSelectionModel().addListSelectionListener(createSelectionListener());

        jTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSnippetAction();
                }
            }
        });

        jTable.getModel().addTableModelListener(createTableModelListener());
        initTablePopupMenu();
    }

    private TableModelListener createTableModelListener() {
        return new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // TODO test: if last row is deleted, select last row -1
                        if (selectedRow >= 0 && (model.getRowCount() - 1) > selectedRow) {
                            jTable.setRowSelectionInterval(selectedRow, selectedRow);
                        }
                    }
                });
            }
        };
    }

    private void initTablePopupMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setFocusable(false);
        final JMenuItem openSnippet = new JMenuItem("Open");
        openSnippet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSnippetAction();
            }
        });

        popupMenu.add(openSnippet);

        final JMenuItem editSnippet = new JMenuItem("Edit");
        editSnippet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSnippetAction();
            }
        });

        popupMenu.add(editSnippet);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSnippetAction();
            }
        });
        popupMenu.add(deleteItem);

        popupMenu.addSeparator();

        JMenuItem copyCodeMenuItem = new JMenuItem("Copy Code");
        copyCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCodeAction();
            }
        });
        popupMenu.add(copyCodeMenuItem);

        JMenuItem openInBrowser = new JMenuItem("Open in browser");
        openInBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSnippetInBrowser();
            }
        });
        popupMenu.add(openInBrowser);

        jTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                selectClickedRow(e);
                showPopup(e);
            }

            private void selectClickedRow(MouseEvent e) {
                int rowIndex = jTable.rowAtPoint(e.getPoint());
                jTable.setRowSelectionInterval(rowIndex, rowIndex);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        jTable.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSnippetAction();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void openSnippetInBrowser() {
        new Browser().openUrl(snippetsTable.getSelectedSnippet().getUrl());
    }

    private void copyCodeAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        clipboardCopy.copy(snippet.getCode());
    }

    private void editSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        framework.showEditSnippetDialog(snippet);
    }

    private void deleteSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();

        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to delete the snippet:\n" + snippet.getTitle(), "Confirm", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {

            new DeleteSnippetOperation(this, snippet).runWithTask(getFramework().getTaskFactory(), "Deleting snippet");
        }
    }

    private void newSnippetAction(Framework framework) {
        framework.showNewSnippetDialog(new Snippet(), null);
    }

    private void openSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        framework.openSnippet(snippet);
    }

    private ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = e.getFirstIndex();
                if (!e.getValueIsAdjusting()) {
                    snippetViewFormPane.removeAll();

                    Snippet snippet = snippetsTable.getSelectedSnippet();
                    // TODO test
                    if (snippet != null) {
                        JComponent snippetViewForm = new SnippetView(snippet).getMainComponent();
                        snippetViewFormPane.add(snippetViewForm);
                        enableButtonsForSnippet();
                    } else {
                        disableButtonsForSnippet();
                    }
                    snippetViewFormPane.revalidate();
                    snippetViewFormPane.repaint();
                }
            }
        };
    }

    protected void enableButtonsForSnippet() {
        editSnippetButton.setEnabled(true);
        deleteSnippetButton.setEnabled(true);
        copyButton.setEnabled(true);
        openInBrowser.setEnabled(true);
    }

    protected void disableButtonsForSnippet() {
        editSnippetButton.setEnabled(false);
        deleteSnippetButton.setEnabled(false);
        copyButton.setEnabled(false);
        openInBrowser.setEnabled(false);
    }

    public SnippetsTable getSnippetsTable() {
        return snippetsTable;
    }

    public JPanel getSnippetViewFormPane() {
        return snippetViewFormPane;
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    @Override
    public void onError(TagMyCodeException e) {
        framework.manageTagMyCodeExceptions(e);
    }

    public Framework getFramework() {
        return framework;
    }

    public void reset() {
        snippetViewFormPane.removeAll();
    }

    public void createUIComponents() {
        filterTextField = new FilterSnippetsTextField(this);
        filterTextField.setMinimumSize(new Dimension(200, 25));
    }

    public void fireSnippetsChanged() {
        snippetsTable.fireSnippetsChanged();
    }
}
