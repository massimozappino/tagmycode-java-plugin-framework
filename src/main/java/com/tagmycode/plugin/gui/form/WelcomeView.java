package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.GuiUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeView extends AbstractGui {
    private JPanel panel;
    private JButton addNewSnippetButton;
    private JButton searchSnippetButton;
    private Framework framework;

    public WelcomeView(final SnippetsPanel snippetsTab) {
        this.framework = snippetsTab.getFramework();
        getMainComponent().setName("welcome view");

        GuiUtil.makeTransparentButton(searchSnippetButton);
        GuiUtil.makeTransparentButton(addNewSnippetButton);

        addNewSnippetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                snippetsTab.newSnippetAction();
            }
        });
        searchSnippetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                framework.showSearchDialog(null);
            }
        });
    }

    @Override
    public JComponent getMainComponent() {
        return panel;
    }

}
