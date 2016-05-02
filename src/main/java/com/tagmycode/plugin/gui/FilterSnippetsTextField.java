package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;

public class FilterSnippetsTextField extends AbstractJFilterSnippetsTextField {

    private SnippetsTab snippetsTab;
    private FilterSnippetsOperation filterSnippetsOperation = null;

    public FilterSnippetsTextField(SnippetsTab snippetsTab) {
        this.snippetsTab = snippetsTab;
        TextPrompt tp7 = new TextPrompt("Filter snippets", this);
        tp7.changeAlpha(0.5f);
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
