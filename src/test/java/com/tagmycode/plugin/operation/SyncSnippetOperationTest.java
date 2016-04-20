package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.SnippetsDeletions;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SyncSnippetOperationTest extends AbstractTest {

    @Test
    public void testOnSuccess() throws Exception {
        SnippetsTab snippetsTabMock = mock(SnippetsTab.class);
        Framework frameworkMock = mock(Framework.class);
        when(snippetsTabMock.getFramework()).thenReturn(frameworkMock);
        TagMyCode tagMyCodeMock = mock(TagMyCode.class);
        when(tagMyCodeMock.isServiceAvailable()).thenReturn(true);
        when(frameworkMock.getTagMyCode()).thenReturn(tagMyCodeMock);
        Data dataMock = mock(Data.class);
        when(dataMock.getSnippets()).thenReturn(resourceGenerate.aSnippetCollection());
        when(frameworkMock.getData()).thenReturn(dataMock);

        SyncSnippetsOperation syncSnippetsOperation = new SyncSnippetsOperation(new SnippetsUpdatePollingProcess(frameworkMock));

        syncSnippetsOperation.performOperation();
        verify(tagMyCodeMock, times(1)).syncSnippets(resourceGenerate.aSnippetCollection(), new SnippetsDeletions());

        syncSnippetsOperation.onSuccess(null);

        verify(frameworkMock, times(1)).snippetsDataChanged();
    }
}