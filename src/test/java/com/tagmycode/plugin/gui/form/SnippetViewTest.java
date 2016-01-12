package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;
import support.ResourceGenerate;

import javax.swing.*;

import static org.junit.Assert.assertEquals;


public class SnippetViewTest extends AbstractTest {

    @Test
    public void testConstructor() throws Exception {
        Snippet snippet = new ResourceGenerate().aSnippet();
        snippet.setCode("line1\nline2\nline3\nline4\nline5");
        SnippetView snippetView = new SnippetView(snippet);
        JEditorPane snippetEditorPane = snippetView.getSnippetEditorPane();
        assertEquals("line1\n" +
                "line2\n" +
                "line3\n" +
                "line4\n" +
                "line5", snippetEditorPane.getText());
        assertEquals(0, snippetEditorPane.getCaretPosition());
        assertEquals(false, snippetEditorPane.isEditable());
        assertEquals("My title", snippetView.getTitle().getText());
    }

}