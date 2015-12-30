package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.AbstractStorage;

public class Storage extends AbstractStorage {

    private HashMapToFile hashMapToFile;

    public Storage() {
        this.hashMapToFile = new HashMapToFile("/tmp/tagmycode_storage.ser");
    }

    @Override
    protected String read(String key) {
        return hashMapToFile.loadValue(key);
    }

    @Override
    protected void write(String key, String value) {
        hashMapToFile.saveValue(key, value);
    }

    @Override
    protected void unset(String key) {
        hashMapToFile.deleteValue(key);
    }
}
