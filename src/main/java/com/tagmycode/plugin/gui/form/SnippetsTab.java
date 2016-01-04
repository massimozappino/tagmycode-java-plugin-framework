package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.*;
import com.tagmycode.plugin.gui.operation.LoadSnippetsOperation;
import com.tagmycode.plugin.gui.operation.RefreshSnippetsOperation;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SnippetsTab extends AbstractGui implements IOnErrorCallback {
    RefreshSnippetsOperation refreshSnippetsOperation;
    private SnippetsJTable snippetsJTable;
    private JPanel snippetViewFormPane;
    private JButton addSnippetButton;
    private JPanel mainPanel;
    private JButton refreshButton;
    private JPanel leftPane;
    private JButton searchButton;
    private JButton editSnippetButton;
    private JButton deleteSnippetButton;
    private JPanel snippetListPane;
    private Framework framework;
    private JTable snippetsTableComponent;
    private ClipboardCopy clipboardCopy = new ClipboardCopy();

    public SnippetsTab(final Framework framework) {
        this.framework = framework;
        snippetViewFormPane.removeAll();
        refreshSnippetsOperation = new RefreshSnippetsOperation(this);
        initSnippetsJTable();

        leftPane.add(snippetsJTable.getMainComponent(), BorderLayout.CENTER);

        initToolBarButtons(framework);
        initPopupMenuForJTextComponents(getMainComponent());
        loadSnippets();
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

        addSnippetButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSnippetAction(framework);
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

        disableButtonsForSnippet();
    }

    private void initSnippetsJTable() {
        snippetsJTable = new SnippetsJTable();
        snippetsTableComponent = (JTable) snippetsJTable.getSnippetsComponent();
        snippetsJTable.getCellSelectionModel().addListSelectionListener(createSelectionListener());

        snippetsTableComponent.addMouseListener(new MouseAdapter() {
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

        snippetsTableComponent.setComponentPopupMenu(popupMenu);
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = snippetsTableComponent.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), snippetsTableComponent));
                        if (rowAtPoint > -1) {
                            snippetsTableComponent.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
    }

    private void copyCodeAction() {
        Snippet snippet = snippetsJTable.getSelectedSnippet();
        clipboardCopy.copy(snippet.getCode());
    }

    private void editSnippetAction() {
        Snippet snippet = snippetsJTable.getSelectedSnippet();
        framework.showSnippetDialog(snippet, null);
    }

    private void deleteSnippetAction() {
        Snippet snippet = snippetsJTable.getSelectedSnippet();
        snippetsJTable.updateWithSnippets(new SnippetCollection());
    }

    private void addSnippetAction(Framework framework) {
        framework.showSnippetDialog(new Snippet(), null);
    }

    private void openSnippetAction() {
        Snippet snippet = snippetsJTable.getSelectedSnippet();
        framework.getMainWindow().addSnippetTab(snippet);
    }

    private ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    snippetViewFormPane.removeAll();

                    Snippet snippet = snippetsJTable.getSelectedSnippet();
                    // TODO test
                    if (snippet != null) {
                        JComponent snippetViewForm = new SnippetForm(snippet).getSnippetEditorPane();
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

    public AbstractSnippetsListGui getAbstractSnippetsListGui() {
        return snippetsJTable;
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

    public RefreshSnippetsOperation getRefreshSnippetsOperation() {
        return refreshSnippetsOperation;
    }
}
