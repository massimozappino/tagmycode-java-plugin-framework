package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.IconResources;
import com.tagmycode.plugin.gui.*;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.plugin.gui.table.TableRowTransferHandler;
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

public class SnippetsPanel extends AbstractGui implements IOnErrorCallback {
    private final WelcomeView welcomeView;
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
    private FilterSnippetsTextField filterTextField;
    private SnippetView snippetView = new SnippetView();

    public SnippetsPanel(final Framework framework) {
        this.framework = framework;
        initSnippetsJTable();
        welcomeView = new WelcomeView(this);

        leftPane.add(snippetsTable.getMainComponent(), BorderLayout.CENTER);
        initFilterField();
        initToolBarButtons(framework);
        initPopupMenuForJTextComponents(getMainComponent());
        configureDragAndDrop();
        reset();
    }

    private void configureDragAndDrop() {
        jTable.setDragEnabled(true);
        jTable.setDropMode(DropMode.INSERT_ROWS);
        jTable.setTransferHandler(new TableRowTransferHandler(this));
        jTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, TransferHandler.COPY);
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
        newSnippetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSnippetAction();
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

        jTable = snippetsTable.getJTable();
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

        addKeyStroke(this.jTable, KeyEvent.VK_F, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterTextField.requestFocus();
            }
        });

        initTablePopupMenu();
    }

    private void addKeyStroke(JComponent jComponent, int keyEvent, ActionListener actionListener) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyEvent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        jComponent.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
        framework.openUrlInBrowser(snippetsTable.getSelectedSnippet().getUrl());
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

    public void newSnippetAction(String code) {
        framework.showNewSnippetDialog(framework.getData().createSnippet("", code, null));
    }

    public void newSnippetAction() {
        newSnippetAction("");
    }

    public void newSnippetAction(Snippet snippet) {
        framework.showNewSnippetDialog(snippet);
    }

    private ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = e.getFirstIndex();
                if (!e.getValueIsAdjusting()) {
                    Snippet snippet = snippetsTable.getSelectedSnippet();
                    changeViewBasedOnSnippet(snippet);
                }
            }
        };
    }

    private void changeViewBasedOnSnippet(Snippet snippet) {
        snippetViewFormPane.removeAll();

        if (snippet != null) {
            snippetView.setSnippet(snippet);
            snippetViewFormPane.add(snippetView.getMainComponent());
            enableButtonsForSnippet();
        } else {
            snippetViewFormPane.add(welcomeView.getMainComponent());
            disableButtonsForSnippet();
        }
        snippetViewFormPane.revalidate();
        snippetViewFormPane.repaint();
        getMainComponent().revalidate();
        getMainComponent().repaint();
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

    public void initFilterField() {
        filterTextField = new FilterSnippetsTextField(framework, snippetsTable);
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
        framework.setNetworkingEnabled(networkingEnabled);
    }

    public void reset() {
        changeViewBasedOnSnippet(null);
    }

    public SnippetsTableModel getSnippetsModel() {
        return model;
    }
}

