package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.AbstractTaskFactory;

public class TaskFactory extends AbstractTaskFactory {
    @Override
    public void create(final Runnable runnable, final String title) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.print(title + "...");
                runnable.run();
                System.out.println("OK");
            }
        });
        thread.start();
    }
}
