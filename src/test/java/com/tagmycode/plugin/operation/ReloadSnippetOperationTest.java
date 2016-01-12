package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ReloadSnippetOperationTest extends AbstractTest {

    @Test
    public void testOnSuccess() throws Exception {
        SnippetsTab snippetsTabMock = mock(SnippetsTab.class);
        ReloadSnippetsOperation reloadSnippetsOperation = new ReloadSnippetsOperation(snippetsTabMock);
        Framework frameworkMock = mock(Framework.class);

        when(snippetsTabMock.getFramework()).thenReturn(frameworkMock);
        SnippetCollection snippets = resourceGenerate.aSnippetCollection();
        reloadSnippetsOperation.onSuccess(snippets);
        verify(frameworkMock, times(1)).updateSnippets(snippets);
    }
}