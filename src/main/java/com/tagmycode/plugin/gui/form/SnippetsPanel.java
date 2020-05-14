package com.tagmycode.plugin.gui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.IconResources;
import com.tagmycode.plugin.UserPreferences;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.ClipboardCopy;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.plugin.gui.MoveUpDownFilterFieldKeyListener;
import com.tagmycode.plugin.gui.filter.FilterSnippetsTextField;
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
import java.io.File;
import java.io.IOException;

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
    private JPanel filterTextPanel;
    private JButton buttonAbout;
    private JButton buttonNetworking;
    private JButton saveAsButton;
    private JPanel filtersPanel;
    private JSplitPane languagesSplitPane;
    private JToggleButton toggleLanguagesFilter;
    private JToolBar leftToolbar;
    private JSplitPane snippetsSplitPane;
    private JPanel jscrollPane;
    private JPanel snippetListPane;
    private Framework framework;
    private JTable jTable;
    private ClipboardCopy clipboardCopy = new ClipboardCopy();
    private SnippetsTableModel model;
    private boolean networkingEnabled;
    private FilterSnippetsTextField filterTextField;
    private SnippetView snippetView;
    private FiltersPanelForm filtersPanelForm;
    private int lastDividerLocation;

    public SnippetsPanel(final Framework framework) {
        this.framework = framework;
        snippetView = new SnippetView(framework.getSyntaxSnippetEditorFactory());
        initSnippetsJTable();
        welcomeView = new WelcomeView(this);

        leftPane.add(snippetsTable.getMainComponent(), BorderLayout.CENTER);
        initFilterPanel();
        initFilterField();
        initToolBarButtons();
        initPopupMenuForJTextComponents(getMainComponent());
        configureDragAndDrop();
        reset();
    }

    private void initFilterPanel() {
        filtersPanelForm = new FiltersPanelForm(getSnippetsTable().getFilterSnippetsOperation(), framework.getData());
        JComponent mainComponent = filtersPanelForm.getMainComponent();
        filtersPanel.add(mainComponent);
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

    private void initToolBarButtons() {
        toggleLanguagesFilter = new JToggleButton(IconResources.createImageIcon("filter.png"), true);
        toggleLanguagesFilter.setToolTipText("Toggle filters");
        if (!framework.getUserPreferences().getBoolean(UserPreferences.TOGGLE_FILTER_BUTTON_SELECTED, true)) {
            hideLanguageFilter();
            toggleLanguagesFilter.setSelected(false);
        }

        leftToolbar.add(toggleLanguagesFilter);

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
        saveAsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsAction();
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
                toggleNetworking();
            }
        });
        toggleLanguagesFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleLanguageFilter();
                framework.getUserPreferences().setBoolean(
                        UserPreferences.TOGGLE_FILTER_BUTTON_SELECTED,
                        toggleLanguagesFilter.isSelected());
            }
        });
        disableButtonsForSnippet();
    }

    private void toggleLanguageFilter() {
        if (languagesSplitPane.getDividerLocation() <= 1) {
            languagesSplitPane.setDividerLocation(lastDividerLocation);
        } else {
            hideLanguageFilter();
        }
    }

    private void hideLanguageFilter() {
        filtersPanelForm.reset();
        lastDividerLocation = languagesSplitPane.getDividerLocation();
        languagesSplitPane.setDividerLocation(0.0d);
    }

    private void toggleNetworking() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean newStatus = !framework.isNetworkingEnabled();
                setNetworkingEnabled(newStatus);
            }
        }).start();
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

        createMenu(popupMenu);

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

    private void createMenu(JPopupMenu popupMenu) {
        createMenuItem(popupMenu, "Edit", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSnippetAction();
            }
        }, "edit.png");

        createMenuItem(popupMenu, "Delete", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSnippetAction();
            }
        }, "delete.png");

        createMenuItem(popupMenu, "Clone snippet...", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cloneSnippetAction();
            }
        }, "clone.png");
        popupMenu.addSeparator();

        createMenuItem(popupMenu, "Copy code", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCodeAction();
            }
        }, "copy.png");

        createMenuItem(popupMenu, "Copy link address", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyLinkAddressAction();
            }
        }, "copy-link.png");

        createMenuItem(popupMenu, "Open in browser", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSnippetInBrowser();
            }
        }, "link.png");

        popupMenu.addSeparator();

        createMenuItem(popupMenu, "Save as...", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsAction();
            }
        }, "save.png");

        popupMenu.addSeparator();

        createMenuItem(popupMenu, "Properties...", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                propertiesAction();
            }
        }, "properties.png");
    }

    private void propertiesAction() {
        new PropertiesForm(framework, snippetsTable.getSelectedSnippet()).display();
    }

    private void cloneSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        removeIdsFromSnippet(snippet);
        framework.showSnippetDialog(snippet);
    }

    private void removeIdsFromSnippet(Snippet snippet) {
        snippet.setId(0);
        snippet.setLocalId(0);
    }

    private void createMenuItem(JPopupMenu popupMenu, String text, ActionListener actionListener, String iconName) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(actionListener);
        menuItem.setIcon(IconResources.createImageIcon(iconName));
        popupMenu.add(menuItem);
    }

    private void copyLinkAddressAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();
        clipboardCopy.copy(snippet.getUrl());
    }

    private void saveAsAction() {
        Snippet selectedSnippet = snippetsTable.getSelectedSnippet();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(selectedSnippet.getTitle()));
        if (fileChooser.showSaveDialog(framework.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                framework.getData().saveSnippetToFile(file, selectedSnippet);
            } catch (IOException e) {
                framework.logError(e);
            }
        }
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
        framework.showSnippetDialog(selectedSnippet);
    }

    private void deleteSnippetAction() {
        Snippet snippet = snippetsTable.getSelectedSnippet();

        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to delete the snippet:\n" + snippet.getTitle(), "Confirm", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            new DeleteSnippetOperation(this, snippet).runWithTask(getFramework().getTaskFactory(), "Deleting snippet");
        }
    }

    public void newSnippetAction(String code) {
        framework.showSnippetDialog(framework.getData().createSnippet("", code, null));
    }

    public void newSnippetAction() {
        newSnippetAction("");
    }

    public void newSnippetAction(Snippet snippet) {
        framework.showSnippetDialog(snippet);
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
        saveAsButton.setEnabled(true);
    }

    protected void disableButtonsForSnippet() {
        editSnippetButton.setEnabled(false);
        deleteSnippetButton.setEnabled(false);
        copyButton.setEnabled(false);
        openInBrowser.setEnabled(false);
        saveAsButton.setEnabled(false);
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
        filterTextField = new FilterSnippetsTextField(snippetsTable.getFilterSnippetsOperation());
        filterTextField.setMinimumSize(new Dimension(200, 25));
        filterTextField.addKeyListener(new MoveUpDownFilterFieldKeyListener(jTable));
        setPlaceholder("Filter snippets", filterTextField);
        filterTextPanel.add(filterTextField);
    }

    public void fireSnippetsChanged() {
        snippetsTable.fireSnippetsChanged();
        filtersPanelForm.refresh();
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, BorderLayout.CENTER);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        panel1.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        toolBar1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        newSnippetButton = new JButton();
        newSnippetButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png")));
        newSnippetButton.setText("");
        newSnippetButton.setToolTipText("New Snippet...");
        toolBar1.add(newSnippetButton);
        editSnippetButton = new JButton();
        editSnippetButton.setIcon(new ImageIcon(getClass().getResource("/icons/edit.png")));
        editSnippetButton.setText("");
        editSnippetButton.setToolTipText("Edit...");
        toolBar1.add(editSnippetButton);
        deleteSnippetButton = new JButton();
        deleteSnippetButton.setIcon(new ImageIcon(getClass().getResource("/icons/delete.png")));
        deleteSnippetButton.setText("");
        deleteSnippetButton.setToolTipText("Delete");
        toolBar1.add(deleteSnippetButton);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator1);
        copyButton = new JButton();
        copyButton.setIcon(new ImageIcon(getClass().getResource("/icons/copy.png")));
        copyButton.setText("");
        copyButton.setToolTipText("Copy code to clipboard");
        toolBar1.add(copyButton);
        openInBrowser = new JButton();
        openInBrowser.setIcon(new ImageIcon(getClass().getResource("/icons/link.png")));
        openInBrowser.setText("");
        openInBrowser.setToolTipText("Open snippet in browser...");
        toolBar1.add(openInBrowser);
        saveAsButton = new JButton();
        saveAsButton.setIcon(new ImageIcon(getClass().getResource("/icons/save.png")));
        saveAsButton.setText("");
        saveAsButton.setToolTipText("Save As...");
        toolBar1.add(saveAsButton);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator2);
        syncButton = new JButton();
        syncButton.setIcon(new ImageIcon(getClass().getResource("/icons/sync.png")));
        syncButton.setText("");
        syncButton.setToolTipText("Sync snippets");
        toolBar1.add(syncButton);
        buttonNetworking = new JButton();
        buttonNetworking.setIcon(new ImageIcon(getClass().getResource("/icons/connected.png")));
        buttonNetworking.setText("");
        buttonNetworking.setToolTipText("Enable/disable auto sync");
        toolBar1.add(buttonNetworking);
        final JToolBar.Separator toolBar$Separator3 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator3);
        settingsButton = new JButton();
        settingsButton.setIcon(new ImageIcon(getClass().getResource("/icons/settings.png")));
        settingsButton.setText("");
        settingsButton.setToolTipText("Settings...");
        toolBar1.add(settingsButton);
        final JToolBar.Separator toolBar$Separator4 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator4);
        buttonAbout = new JButton();
        buttonAbout.setIcon(new ImageIcon(getClass().getResource("/icons/help.png")));
        buttonAbout.setText("");
        buttonAbout.setToolTipText("About...");
        toolBar1.add(buttonAbout);
        final JToolBar.Separator toolBar$Separator5 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator5);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        toolBar1.add(panel2);
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        filterTextPanel = new JPanel();
        filterTextPanel.setLayout(new BorderLayout(0, 0));
        panel2.add(filterTextPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        leftToolbar = new JToolBar();
        leftToolbar.setFloatable(false);
        leftToolbar.setOrientation(1);
        panel3.add(leftToolbar, BorderLayout.WEST);
        leftToolbar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        languagesSplitPane = new JSplitPane();
        languagesSplitPane.setDividerLocation(150);
        languagesSplitPane.setEnabled(true);
        languagesSplitPane.setOneTouchExpandable(false);
        panel3.add(languagesSplitPane, BorderLayout.CENTER);
        snippetsSplitPane = new JSplitPane();
        snippetsSplitPane.setDividerLocation(423);
        languagesSplitPane.setRightComponent(snippetsSplitPane);
        snippetsSplitPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        snippetViewFormPane = new JPanel();
        snippetViewFormPane.setLayout(new BorderLayout(0, 0));
        snippetsSplitPane.setRightComponent(snippetViewFormPane);
        snippetViewFormPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        leftPane = new JPanel();
        leftPane.setLayout(new BorderLayout(0, 0));
        snippetsSplitPane.setLeftComponent(leftPane);
        filtersPanel = new JPanel();
        filtersPanel.setLayout(new BorderLayout(0, 0));
        languagesSplitPane.setLeftComponent(filtersPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}

