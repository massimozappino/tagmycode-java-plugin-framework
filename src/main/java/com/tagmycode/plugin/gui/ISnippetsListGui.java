package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

public interface ISnippetsListGui {
    public void updateWithSnippets(SnippetCollection snippets);

    public Snippet getSelectedSnippet();
}
