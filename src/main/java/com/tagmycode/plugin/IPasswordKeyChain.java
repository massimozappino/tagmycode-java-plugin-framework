package com.tagmycode.plugin;

import com.tagmycode.plugin.exception.TagMyCodeGuiException;

public interface IPasswordKeyChain {
    public void saveValue(String key, String value) throws TagMyCodeGuiException;

    public String loadValue(String key) throws TagMyCodeGuiException;

    public void deleteValue(String key) throws TagMyCodeGuiException;
}
