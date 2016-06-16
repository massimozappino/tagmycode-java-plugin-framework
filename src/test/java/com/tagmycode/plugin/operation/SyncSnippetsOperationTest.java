package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SyncSnippetsOperationTest {
    @Test
    public void onComplete() throws Exception {
        SnippetsUpdatePollingProcess syncProcessMock = mock(SnippetsUpdatePollingProcess.class);
        SyncSnippetsOperation syncSnippetsOperation = new SyncSnippetsOperation(syncProcessMock);
        syncSnippetsOperation.onComplete();
        verify(syncProcessMock, times(1)).syncCompleted();
    }

}