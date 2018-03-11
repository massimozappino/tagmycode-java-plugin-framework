package com.tagmycode.plugin;

import com.tagmycode.plugin.exception.TagMyCodeGuiException;

public interface IPasswordKeyChain {
    void saveValue(String key, String value) throws TagMyCodeGuiException;

    String loadValue(String key) throws TagMyCodeGuiException;

    void deleteValue(String key) throws TagMyCodeGuiException;
}
