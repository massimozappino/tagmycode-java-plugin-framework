package com.tagmycode.plugin.example;

import com.tagmycode.plugin.AbstractPreferences;
import com.tagmycode.plugin.IPasswordKeyChain;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;


public class Preferences extends AbstractPreferences {

    @Override
    protected String read(String key) {
        return null;
    }

    @Override
    protected void write(String key, String value) {

    }

    @Override
    protected void unset(String key) {

    }
}
