package com.tagmycode.plugin;

import java.awt.*;

public class FrameworkConfig {
    private final IPasswordKeyChain passwordManager;
    private final AbstractPreferences preferences;
    private final IMessageManager messageManager;
    private final Frame parentFrame;
    private AbstractTaskFactory task;

    public FrameworkConfig(IPasswordKeyChain passwordManager, AbstractPreferences preferences, IMessageManager messageManager, AbstractTaskFactory task, Frame parentFrame) {
        this.passwordManager = passwordManager;
        this.preferences = preferences;
        this.messageManager = messageManager;
        this.task = task;
        this.parentFrame = parentFrame;
    }

    public IPasswordKeyChain getPasswordKeyChain() {
        return passwordManager;
    }

    public AbstractPreferences getPreferences() {
        return preferences;
    }

    public IMessageManager getMessageManager() {
        return messageManager;
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public AbstractTaskFactory getTask() {
        return task;
    }

}
