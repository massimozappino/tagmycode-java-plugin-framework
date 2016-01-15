package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.ClipboardCopy;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.operation.DeleteSnippetOperation;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;
import com.tagmycode.plugin.operation.LoadSnippetsOperation;
import com.tagmycode.plugin.operation.ReloadSnippetsOperation;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SnippetsTab extends AbstractGui implements IOnErrorCallback {
    private ReloadSnippetsOperation refreshSnippetsOperation;
    private SnippetsTable snippetsTable;
    private JPanel snippetViewFormPane;
    private JButton newSnippetButton;
    private JPanel mainPanel;
    private JButton refreshButton;
    private JPanel leftPane;
    private JButton searchButton;
    private JButton editSnippetButton;
    private JButton deleteSnippetButton;
    private JTextField filterTextField;
    private JButton settingsButton;
    private JPanel snippetListPane;
    private Framework framework;
    private JTable jTable;
    private ClipboardCopy clipboardCopy = new ClipboardCopy();
    private FilterSnippetsOperation filterSnippetsOperation;

    public SnippetsTab(final Framework framework) {
        this.framework = framework;
        snippetViewFormPane.removeAll();
        refreshSnippetsOperation = new ReloadSnippetsOperation(this);
        initSnippetsJTable();

        leftPane.add(snippetsTable.getMainComponent(), BorderLayout.CENTER);

        initToolBarButtons(framework);
        initPopupMenuForJTextComponents(getMainComponent());
        loadSnippets();

        filterTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doFilter();
            }

            private void doFilter() {
                if (filterSnippetsOperation != null) {
                    filterSnippetsOperation.stop();
                }
                String filterText = filterTextField.getText();
                filterSnippetsOperation = new FilterSnippetsOperation(SnippetsTab.this, filterText);
                filterSnippetsOperation.run();
            }
        });
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
        newSnippetButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSnippetAction(framework);
            }
        });
        refreshButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshSnippets();
            }
        });
        searchButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.showSearchDialog(null);
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
        snippetsTable = new SnippetsTable();

        jTable = snippetsTable.getSnippetsComponent();
        snippetsTable.getCellSelectionModel().addListSelectionListener(createSelectionListener());

        jTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSnippetAction();
                }
            }
        });
        initTablePopupMenu();
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
    }

    private void copyCodeAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        clipboardCopy.copy(snippet.getCode());
    }

    private void editSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        framework.showSnippetDialog(snippet, null);
    }

    private void deleteSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();

        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to delete the snippet:\n" + snippet.getTitle(), "Confirm", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {

            new DeleteSnippetOperation(this, snippet).runWithTask(getFramework().getTaskFactory(), "Deleting snippet");
        }
    }

    private void newSnippetAction(Framework framework) {
        framework.showSnippetDialog(new Snippet(), null);
    }

    private void openSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        framework.getMainWindow().addSnippetTab(snippet);
    }

    private ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    snippetViewFormPane.removeAll();

                    Snippet snippet = snippetsTable.getSelectedSnippet();
                    // TODO test
                    if (snippet != null) {
                        JComponent snippetViewForm = new SnippetView(snippet).getSnippetEditorPane();
                        snippetViewFormPane.add(new JScrollPane(snippetViewForm));
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

    private void enableButtonsForSnippet() {
        editSnippetButton.setEnabled(true);
        deleteSnippetButton.setEnabled(true);
    }

    private void disableButtonsForSnippet() {
        editSnippetButton.setEnabled(false);
        deleteSnippetButton.setEnabled(false);
    }

    private void loadSnippets() {
        new LoadSnippetsOperation(this).runWithTask(framework.getTaskFactory(), "Loading snippets");
    }

    private void refreshSnippets() {
        refreshSnippetsOperation.runWithTask(framework.getTaskFactory(), "Refreshing snippets");
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

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public Framework getFramework() {
        return framework;
    }
}
