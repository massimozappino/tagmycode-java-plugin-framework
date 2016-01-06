package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;

public class CreateSnippetOperation extends TagMyCodeAsynchronousOperation<Snippet> {
    private SnippetDialog snippetDialog;

    public CreateSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog);
        this.snippetDialog = snippetDialog;
    }

    @Override
    protected void beforePerformOperation() {
        snippetDialog.getButtonOk().setEnabled(false);
    }

    @Override
    protected Snippet performOperation() throws Exception {
        return snippetDialog.getFramework().getTagMyCode().createSnippet(snippetDialog.createSnippetObject());
    }

    @Override
    protected void onComplete() {
        snippetDialog.getButtonOk().setEnabled(true);
    }

    @Override
    protected void onSuccess(Snippet snippet) {
        String url = snippet.getUrl();
        snippetDialog.getFramework().getConsole().log(String.format("<strong>%s</strong>", snippet.getTitle()) + " " + getHtmlLink(url));
        snippetDialog.closeDialog();
    }

    private String getHtmlLink(String url, String text) {
        return "<a href=\"" + url + "\">" + text + "</a>";
    }

    private String getHtmlLink(String url) {
        return getHtmlLink(url, url);
    }
}
