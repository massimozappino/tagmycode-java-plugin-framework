package com.tagmycode.plugin.operation;

import com.j256.ormlite.dao.Dao;
import com.tagmycode.plugin.gui.form.SnippetsPanel;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;
import support.AbstractTestBase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DeleteSnippetOperationTest extends AbstractTestBase {

    @Test
    public void testPerformOperation() throws Exception {

        SnippetsPanel snippetsTabMock = mock(SnippetsPanel.class);
        Snippet snippetToDelete = resourceGenerate.aSnippet();
        DeleteSnippetOperation deleteSnippetOperation = spy(new DeleteSnippetOperation(snippetsTabMock, snippetToDelete));
        doReturn(mock(Dao.class)).when(deleteSnippetOperation).getSnippetDao();
        doNothing().when(deleteSnippetOperation).fireDataChanged(anyInt());

        assertFalse(snippetToDelete.isDeleted());

        deleteSnippetOperation.performOperation();

        assertTrue(snippetToDelete.isDeleted());
    }
}