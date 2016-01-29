package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.SnippetCollection;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ReloadSnippetOperationTest extends AbstractTest {

    @Test
    public void testOnSuccess() throws Exception {
        SnippetsTab snippetsTabMock = mock(SnippetsTab.class);
        Framework frameworkMock = mock(Framework.class);
        when(snippetsTabMock.getFramework()).thenReturn(frameworkMock);
        TagMyCode tagMyCodeMock = mock(TagMyCode.class);
        when(frameworkMock.getTagMyCode()).thenReturn(tagMyCodeMock);
        Data dataMock = mock(Data.class);
        when(dataMock.getLastSnippetsUpdate()).thenReturn("");
        when(frameworkMock.getData()).thenReturn(dataMock);

        when(tagMyCodeMock.getLastSnippetUpdate()).thenReturn(resourceGenerate.aSnippetsLastUpdate());
        when(tagMyCodeMock.fetchSnippetsCollection()).thenReturn(resourceGenerate.aSnippetCollection());

        ReloadSnippetsOperation reloadSnippetsOperation = new ReloadSnippetsOperation(snippetsTabMock);

        SnippetCollection snippets = resourceGenerate.aSnippetCollection();
        reloadSnippetsOperation.performOperation();
        reloadSnippetsOperation.onSuccess(snippets);

        verify(frameworkMock, times(1)).updateSnippets(snippets);
        verify(frameworkMock, times(1)).updateLastSnippetsUpdate(resourceGenerate.aSnippetsLastUpdate());
    }
}