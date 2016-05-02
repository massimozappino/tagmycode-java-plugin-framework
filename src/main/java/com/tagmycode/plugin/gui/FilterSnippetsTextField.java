package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;

import static com.tagmycode.plugin.gui.GuiUtil.setPlaceholder;

public class FilterSnippetsTextField extends AbstractJFilterSnippetsTextField {

    private SnippetsTab snippetsTab;
    private FilterSnippetsOperation filterSnippetsOperation = null;

    public FilterSnippetsTextField(SnippetsTab snippetsTab) {
        this.snippetsTab = snippetsTab;
        setPlaceholder("Filter snippets", this);
    }

    public void doFilter() {
        if (filterSnippetsOperation != null) {
            filterSnippetsOperation.stop();
        }
        String filterText = getText();
        filterSnippetsOperation = new FilterSnippetsOperation(snippetsTab, filterText);
        filterSnippetsOperation.run();
    }

}
