package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.SnippetsCollection;
import org.junit.Before;
import org.junit.Test;
import support.AbstractTestBase;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


public class FilterSnippetsOperationTest extends AbstractTestBase {
    private SnippetsCollection snippets;

    @Before
    public void setup() throws IOException, TagMyCodeJsonException {
        snippets = new SnippetsCollection();
        snippets.add(resourceGenerate.aSnippet().setLanguage(resourceGenerate.languagePHP()).setTitle("a PHP snippet"));
        snippets.add(resourceGenerate.aSnippet().setLanguage(resourceGenerate.languageJava()).setTitle("a JAVA snippet full text"));
        snippets.add(resourceGenerate.aSnippet().setLanguage(resourceGenerate.languageRuby()).setTitle("a RUBY snippet"));
        snippets.add(resourceGenerate.aSnippet().setLanguage(resourceGenerate.languageJava()).setTitle("a second JAVA snippet"));
    }

    @Test
    public void testSearchText() {
        FilterSnippetsOperation operation = new FilterSnippetsOperation(mock(SnippetsTable.class));
        operation.setSearchText("");
        assertTrue(operation.search("hello", "hello world"));
        assertTrue(operation.search("w", "hello world"));
        assertFalse(operation.search("{ !}", "function() {}"));
        assertFalse(operation.search("hello java", "hello world"));
    }

    @Test
    public void testFilterWithLanguage() {
        FilterSnippetsOperation operation = new FilterSnippetsOperation(createSnippetsTableMock());
        operation.setFilterLanguage(resourceGenerate.languageJava());

        assertEquals("[1, 3]", operation.filterSnippets().toString());
    }

    @Test
    public void testFilterWithNullLanguage() {
        FilterSnippetsOperation operation = new FilterSnippetsOperation(createSnippetsTableMock());

        assertEquals("[0, 1, 2, 3]", operation.filterSnippets().toString());
    }

    @Test
    public void testMixedFilters() {
        FilterSnippetsOperation operation = new FilterSnippetsOperation(createSnippetsTableMock());
        operation.setSearchText("full text");
        operation.setFilterLanguage(resourceGenerate.languageJava());
        assertEquals("[1]", operation.filterSnippets().toString());
    }

    private SnippetsTable createSnippetsTableMock() {
        Framework framework = mock(Framework.class);
        Data data = mock(Data.class);
        doReturn(data).when(framework).getData();
        doReturn(snippets).when(data).getSnippets();
        SnippetsTable snippetsTableMock = mock(SnippetsTable.class);
        doReturn(framework).when(snippetsTableMock).getFramework();
        return snippetsTableMock;
    }
}