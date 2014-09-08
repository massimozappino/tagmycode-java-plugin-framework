package com.tagmycode.plugin;

public abstract class AbstractTaskFactory {
    public abstract void create(final Runnable runnable, String title);
}
