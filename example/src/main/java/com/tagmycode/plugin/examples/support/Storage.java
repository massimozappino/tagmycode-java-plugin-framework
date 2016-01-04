package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.IStorage;

public class Storage implements IStorage {

    private HashMapToFile hashMapToFile;

    public Storage() {
        this.hashMapToFile = new HashMapToFile("/tmp/tagmycode_storage.ser");
    }

    @Override
    public String read(String key) {
        return hashMapToFile.loadValue(key);
    }

    @Override
    public void write(String key, String value) {
        hashMapToFile.saveValue(key, value);
    }

    @Override
    public void unset(String key) {
        hashMapToFile.deleteValue(key);
    }
}
