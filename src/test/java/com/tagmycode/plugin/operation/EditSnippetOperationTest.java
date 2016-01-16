package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EditSnippetOperationTest extends AbstractTest {

    @Test
    public void testPerformOperation() throws Exception {
        SnippetDialog snippetDialogMock = mock(SnippetDialog.class);
        when(snippetDialogMock.createSnippetObject()).thenReturn(resourceGenerate.aSnippet());
        EditSnippetOperation editSnippetOperation = new EditSnippetOperation(snippetDialogMock);
        Framework frameworkMock = mock(Framework.class);
        when(snippetDialogMock.getFramework()).thenReturn(frameworkMock);
        TagMyCode tagMyCode = mock(TagMyCode.class);
        when(frameworkMock.getTagMyCode()).thenReturn(tagMyCode);

        Snippet snippet = resourceGenerate.aSnippet();

        editSnippetOperation.performOperation();

        verify(tagMyCode, times(1)).updateSnippet(snippet);
    }

    @Test
    public void testOnSuccess() throws Exception {
        SnippetDialog snippetDialogMock = mock(SnippetDialog.class);
        EditSnippetOperation editSnippetOperation = new EditSnippetOperation(snippetDialogMock);
        Framework frameworkMock = mock(Framework.class);
        when(snippetDialogMock.getFramework()).thenReturn(frameworkMock);
        Snippet snippet = resourceGenerate.aSnippet();

        editSnippetOperation.onSuccess(snippet);

        verify(snippetDialogMock, times(1)).closeDialog();
        verify(frameworkMock, times(1)).updateSnippet(snippet);
    }
}