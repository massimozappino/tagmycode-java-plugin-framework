package com.tagmycode.plugin.gui.table;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateSnippetTableCellRenderTest {
    @Test
    public void customLabel() throws Exception {
        DateSnippetTableCellRender dateSnippetTableCellRender = new DateSnippetTableCellRender();

        dateSnippetTableCellRender.customLabel(null);
        assertEquals("", dateSnippetTableCellRender.label.getText());
    }

}