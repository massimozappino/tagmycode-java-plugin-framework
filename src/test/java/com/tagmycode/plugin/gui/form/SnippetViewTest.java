package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.SyntaxSnippetEditorFactory;
import com.tagmycode.sdk.model.Snippet;
import org.fife.ui.rtextarea.RTextArea;
import org.junit.Test;
import support.AbstractTestBase;
import support.ResourceGenerate;

import static org.junit.Assert.assertEquals;


public class SnippetViewTest extends AbstractTestBase {

    @Test
    public void testConstructor() throws Exception {
        Snippet snippet = new ResourceGenerate().aSnippet();
        snippet.setCode("line1\nline2\nline3\nline4\nline5");
        SnippetView snippetView = new SnippetView(new SyntaxSnippetEditorFactory("", 13)).setSnippet(snippet);
        RTextArea snippetEditorPane = snippetView.getSnippetEditorPane().getTextArea();
        assertEquals("line1\n" +
                "line2\n" +
                "line3\n" +
                "line4\n" +
                "line5", snippetEditorPane.getText());
        assertEquals(0, snippetEditorPane.getCaretPosition());
        assertEquals(false, snippetEditorPane.isEditable());
    }

}