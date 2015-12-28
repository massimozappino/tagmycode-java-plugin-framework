package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.IMessageManager;

public class MessageManager implements IMessageManager {
    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
