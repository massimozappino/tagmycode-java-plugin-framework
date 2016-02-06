package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;

public interface ISnippetsListGui {
    public void fireSnippetsChanged();

    public Snippet getSelectedSnippet();
}
