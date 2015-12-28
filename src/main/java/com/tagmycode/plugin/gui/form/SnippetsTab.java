package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractForm;
import com.tagmycode.plugin.gui.SnippetsJList;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;

public class SnippetsTab extends AbstractForm {
    private SnippetsJList snippetsJList;
    private JPanel snippetViewFormPanel;
    private JButton addSnippetButton;
    private JPanel mainPanel;
    private JTextField textField1;

    public SnippetsTab(final Framework framework) {
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
}
