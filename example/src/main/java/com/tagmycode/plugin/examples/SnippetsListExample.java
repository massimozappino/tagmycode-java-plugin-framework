package com.tagmycode.plugin.examples;

import com.tagmycode.plugin.gui.SnippetsJList;
import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;

public class SnippetsListExample {
    public static void main(String args[]) {
        JFrame frame = new JFrame();

        SnippetsJList snippetsJList = new SnippetsJList();
        ModelCollection<Snippet> snippets = new ModelCollection<Snippet>();
        snippets.add(new Snippet().setCode("code1").setTitle("title1"));
        snippets.add(new Snippet().setCode("code2").setTitle("title2"));
        snippetsJList.updateWithSnippets(snippets);
        frame.add(snippetsJList);
        frame.setVisible(true);
        frame.pack();
        frame.setSize(800, 600);
    }
}
