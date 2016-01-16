package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class NewSnippetOperationTest extends AbstractTest {

    @Test
    public void testOnSuccess() throws Exception {
        SnippetDialog snippetDialogMock = mock(SnippetDialog.class);
        NewSnippetOperation newSnippetOperation = new NewSnippetOperation(snippetDialogMock);
        Framework frameworkMock = mock(Framework.class);
        when(snippetDialogMock.getFramework()).thenReturn(frameworkMock);
        Snippet snippet = resourceGenerate.aSnippet();

        newSnippetOperation.onSuccess(snippet);

        verify(snippetDialogMock, times(1)).closeDialog();
        verify(frameworkMock, times(1)).addSnippet(snippet);
    }
}