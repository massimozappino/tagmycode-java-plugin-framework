package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.IPasswordKeyChain;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;

import java.util.HashMap;


public class PasswordKeyChain implements IPasswordKeyChain {
    HashMap<String, String> storage = new HashMap<String, String>();

    @Override
    public void saveValue(String key, String value) throws TagMyCodeGuiException {
        storage.put(key, value);
        System.out.println(String.format("key: %s, value: %s", key, value));
    }

    @Override
    public String loadValue(String key) throws TagMyCodeGuiException {
        return storage.get(key);
    }

    @Override
    public void deleteValue(String key) throws TagMyCodeGuiException {
        storage.remove(key);
    }
}
