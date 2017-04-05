package com.tagmycode.plugin;

import com.tagmycode.sdk.DbService;

import java.awt.*;

public class FrameworkConfig {
    private final IPasswordKeyChain passwordManager;
    private final DbService dbService;
    private final IMessageManager messageManager;
    private final IBrowser browser;
    private final Frame parentFrame;
    private AbstractTaskFactory task;
    private AbstractVersion versionObject;

    public FrameworkConfig(IPasswordKeyChain passwordManager, DbService dbService, IMessageManager messageManager, AbstractTaskFactory task, AbstractVersion versionObject, Frame parentFrame) {
        this(passwordManager, dbService, messageManager, task, new Browser(), versionObject, parentFrame);
    }

    public FrameworkConfig(IPasswordKeyChain passwordManager, DbService dbService, IMessageManager messageManager, AbstractTaskFactory task, IBrowser browser, AbstractVersion versionObject, Frame parentFrame) {
        this.passwordManager = passwordManager;
        this.dbService = dbService;
        this.messageManager = messageManager;
        this.task = task;
        this.browser = browser;
        this.parentFrame = parentFrame;
        this.versionObject = versionObject;
    }

    public IPasswordKeyChain getPasswordKeyChain() {
        return passwordManager;
    }

    public DbService getDbService() {
        return dbService;
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

    public AbstractVersion getVersionObject() {
        return versionObject;
    }
}
