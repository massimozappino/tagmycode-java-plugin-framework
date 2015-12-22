package com.tagmycode.plugin.example;

import com.tagmycode.plugin.IPasswordKeyChain;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;


public class Preferences implements IPasswordKeyChain {
    @Override
    public void saveValue(String key, String value) throws TagMyCodeGuiException {

    }

    @Override
    public String loadValue(String key) throws TagMyCodeGuiException {
        return null;
    }

    @Override
    public void deleteValue(String key) throws TagMyCodeGuiException {

    }
}
