package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.filter.FilterLanguagesPanel;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;

import javax.swing.*;

public class FiltersPanelForm extends AbstractGui {
    private JPanel languagesPanel;
    private JSplitPane mainComponent;
    private FilterLanguagesPanel filterLanguagesPanel;

    public FiltersPanelForm(FilterSnippetsOperation filterSnippetsOperation, Data data) {
        filterLanguagesPanel = new FilterLanguagesPanel(filterSnippetsOperation, data, languagesPanel);
    }

    @Override
    public JComponent getMainComponent() {
        return mainComponent;
    }

    public void refresh() {
        filterLanguagesPanel.refresh();
    }
}
