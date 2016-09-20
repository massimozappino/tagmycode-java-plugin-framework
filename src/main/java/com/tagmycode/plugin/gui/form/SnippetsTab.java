package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.IconResources;
import com.tagmycode.plugin.gui.*;
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

import static com.tagmycode.plugin.gui.GuiUtil.setPlaceholder;

public class SnippetsTab extends AbstractGui implements IOnErrorCallback {
    protected JButton editSnippetButton;
    protected JButton deleteSnippetButton;
    protected JButton copyButton;
    protected JButton openInBrowser;
    protected int selectedRow = -1;
    private SnippetsTable snippetsTable;
    private JPanel snippetViewFormPane;
    private JButton newSnippetButton;
    private JPanel mainPanel;
    private JButton syncButton;
    private JPanel leftPane;
    private JButton settingsButton;
    private JPanel filterPanel;
    private JButton buttonAbout;
    private JButton buttonNetworking;
    private JPanel snippetListPane;
    private Framework framework;
    private JTable jTable;
    private ClipboardCopy clipboardCopy = new ClipboardCopy();
    private SnippetsTableModel model;
    private boolean networkingEnabled;

    public SnippetsTab(final Framework framework) {
        this.framework = framework;
        reset();
        initSnippetsJTable();

        leftPane.add(snippetsTable.getMainComponent(), BorderLayout.CENTER);
        initFilterField();
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
        syncButton.addActionListener(new AbstractAction() {
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
        buttonAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAboutDialog();
            }
        });

        buttonNetworking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean newStatus = !framework.isNetworkingEnabled();
                        setNetworkingEnabled(newStatus);
                        framework.setNetworkingEnabled(newStatus);
                    }
                }).start();
            }
        });
        disableButtonsForSnippet();
    }

    public void setNetworkIcon(boolean status) {
        String icon = status ? "connected" : "disconnected";
        buttonNetworking.setIcon(IconResources.createImageIcon(icon + ".png"));
    }

    private void openAboutDialog() {
        framework.showAboutDialog();
    }

    private void initSnippetsJTable() {
        snippetsTable = new SnippetsTable(framework);

        jTable = snippetsTable.getSnippetsComponent();
        model = (SnippetsTableModel) jTable.getModel();
        snippetsTable.getCellSelectionModel().addListSelectionListener(createSelectionListener());
        jTable.getModel().addTableModelListener(createTableModelListener());
        jTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSnippetAction();
                }
            }
        });

        initTablePopupMenu();
    }

    private void initTablePopupMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setFocusable(false);

        final JMenuItem editMenuItem = new JMenuItem("Edit");
        editMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSnippetAction();
            }
        });
        editMenuItem.setIcon(IconResources.createImageIcon("edit.png"));
        popupMenu.add(editMenuItem);

        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSnippetAction();
            }
        });
        deleteMenuItem.setIcon(IconResources.createImageIcon("delete.png"));
        popupMenu.add(deleteMenuItem);

        popupMenu.addSeparator();

        JMenuItem copyCodeMenuItem = new JMenuItem("Copy Code");
        copyCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCodeAction();
            }
        });
        copyCodeMenuItem.setIcon(IconResources.createImageIcon("copy.png"));
        popupMenu.add(copyCodeMenuItem);

        JMenuItem openInBrowserMenuItem = new JMenuItem("Open in browser");
        openInBrowserMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSnippetInBrowser();
            }
        });
        openInBrowserMenuItem.setIcon(IconResources.createImageIcon("link.png"));
        popupMenu.add(openInBrowserMenuItem);

        jTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    selectClickedRow(e);
                    showPopup(e);
                }
            }

            private void selectClickedRow(MouseEvent e) {
                int rowIndex = jTable.rowAtPoint(e.getPoint());
                jTable.setRowSelectionInterval(rowIndex, rowIndex);
            }

            private void showPopup(MouseEvent e) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
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
        Snippet selectedSnippet = snippetsTable.getSelectedSnippet();
        framework.showEditSnippetDialog(selectedSnippet);
    }

    private void deleteSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();

        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to delete the snippet:\n" + snippet.getTitle(), "Confirm", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            new DeleteSnippetOperation(this, snippet).runWithTask(getFramework().getTaskFactory(), "Deleting snippet");
        }
    }

    protected void newSnippetAction(Framework framework) {
        framework.showNewSnippetDialog(createEmptySnippet(framework));
    }

    protected Snippet createEmptySnippet(Framework framework) {
        Snippet snippet = new Snippet();
        snippet.setLanguage(framework.getStorageEngine().loadLastLanguageUsed());
        return snippet;
    }

    private ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = e.getFirstIndex();
                if (!e.getValueIsAdjusting()) {
                    snippetViewFormPane.removeAll();

                    Snippet snippet = snippetsTable.getSelectedSnippet();

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

    private TableModelListener createTableModelListener() {
        return new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // TODO test: if last row is deleted, select last row -1
                        if (selectedRow >= 0 && (model.getRowCount() - 1) > selectedRow) {
                            try {
                                jTable.setRowSelectionInterval(selectedRow, selectedRow);
                            } catch (IllegalArgumentException e) {
                                framework.logError(e);
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
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

    public void initFilterField() {
        FilterSnippetsTextField filterTextField = new FilterSnippetsTextField(framework, snippetsTable);
        filterTextField.setMinimumSize(new Dimension(200, 25));
        filterTextField.addKeyListener(new MoveUpDownFilterFieldKeyListener(jTable));
        setPlaceholder("Filter snippets", filterTextField);
        filterPanel.add(filterTextField);
    }

    public void fireSnippetsChanged() {
        snippetsTable.fireSnippetsChanged();
    }

    public JButton getButtonNetworking() {
        return buttonNetworking;
    }

    public void setNetworkingEnabled(boolean networkingEnabled) {
        setNetworkIcon(networkingEnabled);
        syncButton.setEnabled(networkingEnabled);
    }
}
