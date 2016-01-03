package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.AbstractSnippetsListGui;
import com.tagmycode.plugin.gui.IonErrorCallback;
import com.tagmycode.plugin.gui.SnippetsJTable;
import com.tagmycode.plugin.gui.operation.LoadSnippetsOperation;
import com.tagmycode.plugin.gui.operation.RefreshSnippetsOperation;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SnippetsTab extends AbstractGui implements IonErrorCallback {
    private AbstractSnippetsListGui abstractSnippetsListGui;
    private JPanel snippetViewFormPane;
    private JButton addSnippetButton;
    private JPanel mainPanel;
    private JButton refreshButton;
    private JPanel leftPane;
    private JButton searchButton;
    private JPanel snippetListPane;
    private Framework framework;

    public SnippetsTab(final Framework framework) {
        this.framework = framework;
        snippetViewFormPane.removeAll();
        ListSelectionListener listener = createListener();

        abstractSnippetsListGui = new SnippetsJTable();
        ((SnippetsJTable) abstractSnippetsListGui).getCellSelectionModel().addListSelectionListener(listener);

        abstractSnippetsListGui.getSnippetsComponent().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Snippet snippet = abstractSnippetsListGui.getSelectedSnippet();
                    framework.getMainWindow().addSnippetTab(snippet);
                }
            }
        });

        leftPane.add(abstractSnippetsListGui.getMainComponent(), BorderLayout.CENTER);

        addSnippetButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.showSnippetDialog(new Snippet(), null);
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

        initPopupMenuForJTextComponents(getMainComponent());
        loadSnippets();
    }

    private ListSelectionListener createListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    snippetViewFormPane.removeAll();

                    Snippet snippet = abstractSnippetsListGui.getSelectedSnippet();
                    if (snippet != null) {
                        JComponent snippetViewForm = new SnippetForm(snippet).getMainComponent();
                        snippetViewFormPane.add(snippetViewForm);
                    }
                    snippetViewFormPane.revalidate();
                    snippetViewFormPane.repaint();
                }
            }
        };
    }

    private void loadSnippets() {
        new LoadSnippetsOperation(this).runWithTask(framework.getTaskFactory(), "Loading snippets");
    }

    private void refreshSnippets() {
        new RefreshSnippetsOperation(this).runWithTask(framework.getTaskFactory(), "Refreshing snippets");
    }

    public AbstractSnippetsListGui getAbstractSnippetsListGui() {
        return abstractSnippetsListGui;
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
