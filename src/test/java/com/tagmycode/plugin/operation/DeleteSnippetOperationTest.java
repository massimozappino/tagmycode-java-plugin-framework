package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;
import support.AbstractTestBase;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DeleteSnippetOperationTest extends AbstractTestBase {

    @Test
    public void testOnSuccess() throws Exception {
        SnippetsTab snippetsTabMock = mock(SnippetsTab.class);
        Snippet snippetToDelete = resourceGenerate.aSnippet();
        DeleteSnippetOperation deleteSnippetOperation = new DeleteSnippetOperation(snippetsTabMock, snippetToDelete);

        Framework frameworkMock = mock(Framework.class);
        TagMyCode tagMyCodeMock = mock(TagMyCode.class);
        when(frameworkMock.getTagMyCode()).thenReturn(tagMyCodeMock);
        when(snippetsTabMock.getFramework()).thenReturn(frameworkMock);

        assertEquals(snippetToDelete, deleteSnippetOperation.performOperation());
        verify(tagMyCodeMock, times(1)).deleteSnippet(snippetToDelete.getId());

        deleteSnippetOperation.onSuccess(snippetToDelete);
        verify(frameworkMock, times(1)).deleteSnippet(snippetToDelete);
    }
}