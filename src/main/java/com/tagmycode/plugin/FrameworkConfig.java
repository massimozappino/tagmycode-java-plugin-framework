package com.tagmycode.plugin;

import java.awt.*;

public class FrameworkConfig {
    private final IPasswordKeyChain passwordManager;
    private final AbstractPreferences preferences;
    private final IMessageManager messageManager;
    private final Frame mainFrame;
    private AbstractTaskFactory task;

    public FrameworkConfig(IPasswordKeyChain passwordManager, AbstractPreferences preferences, IMessageManager messageManager, AbstractTaskFactory task, Frame mainFrame) {
        this.passwordManager = passwordManager;
        this.preferences = preferences;
        this.messageManager = messageManager;
        this.task = task;
        this.mainFrame = mainFrame;
    }

    public IPasswordKeyChain getPasswordManager() {
        return passwordManager;
    }

    public AbstractPreferences getPreferences() {
        return preferences;
    }

    public IMessageManager getMessageManager() {
        return messageManager;
    }

    public Frame getMainFrame() {
        return mainFrame;
    }

    public AbstractTaskFactory getTask() {
        return task;
    }

}
