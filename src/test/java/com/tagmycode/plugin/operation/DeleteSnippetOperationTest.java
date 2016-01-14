package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class DeleteSnippetOperationTest extends AbstractTest{

    @Test
    public void testOnSuccess() throws Exception {
        SnippetsTab snippetsTabMock = mock(SnippetsTab.class);
        DeleteSnippetOperation deleteSnippetOperation = new DeleteSnippetOperation(snippetsTabMock);
        Framework frameworkMock = mock(Framework.class);

//        when(snippetsTabMock.getFramework()).thenReturn(frameworkMock);
//        deleteSnippetOperation.onSuccess(snippets);
//        verify(frameworkMock, times(1)).updateSnippets(snippets);
    }
}