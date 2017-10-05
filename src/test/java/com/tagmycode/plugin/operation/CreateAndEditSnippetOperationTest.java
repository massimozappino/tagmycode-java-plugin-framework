package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;
import support.AbstractTestBase;

import static org.mockito.Mockito.*;

public class CreateAndEditSnippetOperationTest extends AbstractTestBase {

    @Test
    public void testOnSuccessForceScheduleUpdate() throws Exception {
        Framework frameworkMock = mock(Framework.class);
        SnippetDialog snippetDialogMock = mock(SnippetDialog.class);
        when(snippetDialogMock.getFramework()).thenReturn(frameworkMock);
        SnippetsUpdatePollingProcess snippetsUpdatePollingProcess = mock(SnippetsUpdatePollingProcess.class);
        when(frameworkMock.getPollingProcess()).thenReturn(snippetsUpdatePollingProcess);

        EditSnippetOperation createAndEditSnippetOperation = new EditSnippetOperation(snippetDialogMock);
        Snippet snippet = resourceGenerate.aSnippet();

        createAndEditSnippetOperation.onSuccess(snippet);

        verify(snippetDialogMock, times(1)).closeDialog();
        verify(snippetsUpdatePollingProcess, times(1)).forceScheduleUpdate();
    }
}