package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.gui.form.QuickSearchDialog;
import com.tagmycode.plugin.operation.QuickFilterSnippetsOperation;

public class QuickFilterSnippetsTextField extends AbstractJFilterSnippetsTextField {

    private IDocumentInsertText documentInsertText;
    private QuickSearchDialog quickSearchDialog;
    private QuickFilterSnippetsOperation operation;

    public QuickFilterSnippetsTextField(QuickSearchDialog quickSearchDialog) {
        this.quickSearchDialog = quickSearchDialog;
    }

    public void setDocumentInsertText(IDocumentInsertText documentInsertText) {
        this.documentInsertText = documentInsertText;
    }

    public void doFilter() {
        if (operation != null) {
            operation.stop();
        }
        String filterText = getText();
        operation = new QuickFilterSnippetsOperation(quickSearchDialog, filterText);
        operation.run();
    }

}
