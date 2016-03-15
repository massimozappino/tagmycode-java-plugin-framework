package com.tagmycode.plugin;


public class SnippetsUpdatePollingProcess {
    private boolean exitStatus;
    private Thread thread;

    public SnippetsUpdatePollingProcess() {
        exitStatus = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!exitStatus) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });
    }

    public void start() {
        thread.start();
    }

    public void terminate() {
        this.exitStatus = true;
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
    }
}
