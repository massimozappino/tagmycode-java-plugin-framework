package com.tagmycode.plugin.gui;

import com.tagmycode.sdk.model.Snippet;


public class CustomTextSnippetItem extends Snippet {
    private String text;

    public CustomTextSnippetItem(String text) {

        this.text = text;
    }

    public String getText() {
        return text;
    }
}
