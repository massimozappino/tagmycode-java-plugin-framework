package com.tagmycode.plugin;

public interface IStorage {

    abstract String read(String key);

    abstract void write(String key, String value);

    abstract void unset(String key);
}
