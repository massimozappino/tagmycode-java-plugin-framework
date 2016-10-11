package com.tagmycode.plugin;

import java.awt.*;

public class FrameworkConfig {
    private final IPasswordKeyChain passwordManager;
    private final IStorage storage;
    private final IMessageManager messageManager;
    private final IBrowser browser;
    private final Frame parentFrame;
    private AbstractTaskFactory task;

    public FrameworkConfig(IPasswordKeyChain passwordManager, IStorage storage, IMessageManager messageManager, AbstractTaskFactory task, Frame parentFrame) {
        this(passwordManager, storage, messageManager, task, new Browser(), parentFrame);
    }

    public FrameworkConfig(IPasswordKeyChain passwordManager, IStorage storage, IMessageManager messageManager, AbstractTaskFactory task, IBrowser browser, Frame parentFrame) {
        this.passwordManager = passwordManager;
        this.storage = storage;
        this.messageManager = messageManager;
        this.task = task;
        this.browser = browser;
        this.parentFrame = parentFrame;
    }

    public IPasswordKeyChain getPasswordKeyChain() {
        return passwordManager;
    }

    public IStorage getStorage() {
        return storage;
    }

    public IMessageManager getMessageManager() {
        return messageManager;
    }

    public IBrowser getBrowser() {
        return browser;
    }

    public AbstractTaskFactory getTask() {
        return task;
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

}
