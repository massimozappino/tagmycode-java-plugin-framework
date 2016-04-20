package com.tagmycode.plugin;


import com.tagmycode.plugin.operation.SyncSnippetsOperation;

public class SnippetsUpdatePollingProcess {
    public static final int POLLING_MILLISECONDS = 60 * 60 * 1000;
    public static final int CHECK_STATUS_INTERVAL = 1500;
    public static final int SYNC_NEVER = -1;
    public static final int SYNC_FORCE = 0;
    public long lastSync = -1;
    private boolean exitStatus = false;
    private Thread thread = null;
    private Framework framework;
    private boolean syncingFlag = false;

    public SnippetsUpdatePollingProcess(final Framework framework) {
        this.framework = framework;
    }

    public void start() {
        exitStatus = false;
        scheduleUpdate();
        createThread();
        thread.start();
    }

    public void terminate() {
        if (thread == null) {
            return;
        }
        Framework.LOGGER.info("TERMINATING POLLING");
        this.exitStatus = true;
        syncingFlag = false;
        lastSync = SYNC_NEVER;
        try {
            thread.join();
            Framework.LOGGER.info("POLLING TERMINATED");
        } catch (InterruptedException ignored) {
        } finally {
            thread = null;
        }
    }

    public void forceScheduleUpdate() {
        if (!syncingFlag) {
            scheduleUpdate();
        }
    }

    public void scheduleUpdate() {
        lastSync = SYNC_FORCE;
    }

    private void createThread() {
        if (thread != null) {
            terminate();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!exitStatus) {
                    try {
                        if (isElapsedTime()) {
                            executeTask();
                        }
                        Thread.sleep(CHECK_STATUS_INTERVAL);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });
    }

    private boolean isElapsedTime() {
        if (lastSync == SYNC_NEVER) {
            return false;
        }
        if (lastSync == SYNC_FORCE) {
            return true;
        }

        return now() - lastSync > POLLING_MILLISECONDS;
    }

    private long now() {
        return System.currentTimeMillis();
    }

    public synchronized void syncCompleted() {
        lastSync = now();
        syncingFlag = false;
    }

    private SyncSnippetsOperation createSyncSnippetsOperation() {
        return new SyncSnippetsOperation(this);
    }

    private synchronized void executeTask() {
        syncingFlag = true;
        lastSync = SYNC_NEVER;
        createSyncSnippetsOperation().runWithTask(framework.getTaskFactory(), "Syncing snippets");
    }

    public Framework getFramework() {
        return framework;
    }
}
