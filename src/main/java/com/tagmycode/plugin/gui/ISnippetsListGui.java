package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.TableModelSnippetNotFoundException;
import com.tagmycode.sdk.model.Snippet;

public interface ISnippetsListGui {
    void fireSnippetsChanged();

    Snippet getSelectedSnippet() throws TableModelSnippetNotFoundException;
}
