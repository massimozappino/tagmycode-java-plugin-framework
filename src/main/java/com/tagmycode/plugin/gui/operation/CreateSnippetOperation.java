package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.gui.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;

public class CreateSnippetOperation extends TagMyCodeAsynchronousOperation<String> {
    private SnippetDialog snippetDialog;
    private Snippet obtainedSnippet;

    public CreateSnippetOperation(SnippetDialog snippetDialog) {
        super(snippetDialog);
        this.snippetDialog = snippetDialog;
    }

    @Override
    protected void beforePerformOperation() {
        snippetDialog.getButtonOk().setEnabled(false);
    }

    @Override
    protected String performOperation() throws Exception {
        obtainedSnippet = snippetDialog.getFramework().getTagMyCode().createSnippet(snippetDialog.getSnippet());
        return null;
    }

    @Override
    protected void onComplete() {
        snippetDialog.getButtonOk().setEnabled(true);
    }

    @Override
    protected void onSuccess(String result) {
        String url = obtainedSnippet.getUrl();
        snippetDialog.getFramework().getConsole().log(String.format("<strong>%s</strong>", obtainedSnippet.getTitle()) + " " + getHtmlLink(url));
        snippetDialog.closeDialog();
    }

    private String getHtmlLink(String url, String text) {
        return "<a href=\"" + url + "\">" + text + "</a>";
    }

    private String getHtmlLink(String url) {
        return getHtmlLink(url, url);
    }
}
