package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


public class FilterSnippetsOperationTest {

    @Test
    public void testFilterMatch() throws Exception {
        FilterSnippetsOperation operation = new FilterSnippetsOperation(mock(Framework.class), mock(SnippetsTable.class), "");
        assertTrue(operation.search("hello", "hello world"));
        assertTrue(operation.search("w", "hello world"));
        assertFalse(operation.search("{ !}", "function() {}"));
        assertFalse(operation.search("hello java", "hello world"));
    }
}