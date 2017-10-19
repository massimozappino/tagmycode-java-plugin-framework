package com.tagmycode.plugin;

import javax.swing.*;

public class BackgroundWorker extends SwingWorker<Integer, String> {
    private Runnable runnable;

    public BackgroundWorker(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        runnable.run();
        return 0;
    }

}
