package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.AbstractTaskFactory;


public class TaskFactory extends AbstractTaskFactory {
    @Override
    public void create(Runnable runnable, String title) {
        runnable.run();
    }
}
