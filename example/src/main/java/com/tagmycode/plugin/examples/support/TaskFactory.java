package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.AbstractTaskFactory;

public class TaskFactory extends AbstractTaskFactory {
    @Override
    public void create(final Runnable runnable, String title) {
        System.out.println(title);

        new Thread(runnable).start();

    }
}
