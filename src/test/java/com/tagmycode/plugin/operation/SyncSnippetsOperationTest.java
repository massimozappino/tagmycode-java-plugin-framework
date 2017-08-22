package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.plugin.StorageEngine;
import org.junit.Test;
import support.BaseTest;

import static org.mockito.Mockito.*;

public class SyncSnippetsOperationTest extends BaseTest {
    @Test
    public void onComplete() throws Exception {
        SnippetsUpdatePollingProcess syncProcessMock = mock(SnippetsUpdatePollingProcess.class);
        Framework framework = mock(Framework.class);
        when(syncProcessMock.getFramework()).thenReturn(framework);
        when(framework.getStorageEngine()).thenReturn(mock(StorageEngine.class));
        SyncSnippetsOperation syncSnippetsOperation = new SyncSnippetsOperation(syncProcessMock);

        syncSnippetsOperation.onComplete();

        verify(syncProcessMock, times(1)).syncCompleted();
    }

}