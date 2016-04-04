package com.tagmycode.plugin.gui;

public class QuickFilterSnippetsTextField extends AbstractJFilterSnippetsTextField {

    private IDocumentInsertText iDocumentInsertText;

    public QuickFilterSnippetsTextField(IDocumentInsertText iDocumentInsertText) {
        this.iDocumentInsertText = iDocumentInsertText;
    }

    public void doFilter() {
        System.out.println(getText());
    }

}
