package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractForm;
import com.tagmycode.plugin.gui.IonErrorCallback;
import com.tagmycode.plugin.gui.SnippetsJList;
import com.tagmycode.plugin.gui.operation.RefreshSnippetOperation;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;

public class SnippetsTab extends AbstractForm implements IonErrorCallback {
    private SnippetsJList snippetsJList;
    private JPanel snippetViewFormPanel;
    private JButton addSnippetButton;
    private JPanel mainPanel;
    private JButton refreshButton;
    private Framework framework;

    public SnippetsTab(final Framework framework) {
        this.framework = framework;
        snippetViewFormPanel.removeAll();

        snippetsJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    snippetViewFormPanel.removeAll();

                    Snippet snippet = snippetsJList.getSelectedSnippet();
                    if (snippet != null) {
                        JPanel snippetViewForm = new SnippetForm(snippet).getMainPanel();
                        snippetViewFormPanel.add(snippetViewForm);
                    }
                    snippetViewFormPanel.revalidate();
                    snippetViewFormPanel.repaint();
                }
            }
        });

        addSnippetButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mimeType = "text/plain";
                framework.showSnippetDialog(new Snippet(), mimeType);
            }
        });

        refreshButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshSnippets();
            }
        });
    }

    private void refreshSnippets() {
        new RefreshSnippetOperation(this).runWithTask(framework.getTaskFactory(), "Refreshing snippets");
    }

    public SnippetsJList getSnippetsJList() {
        return snippetsJList;
    }

    public JPanel getSnippetViewFormPanel() {
        return snippetViewFormPanel;
    }

    @Override
    public JPanel getMainPanel() {
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
