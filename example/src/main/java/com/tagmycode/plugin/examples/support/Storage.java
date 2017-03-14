package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.IStorage;
import com.tagmycode.sdk.SaveFilePath;

import java.io.File;
import java.io.IOException;

public class Storage implements IStorage {

    private HashMapToFile hashMapToFile;

    public Storage(SaveFilePath saveFilePath) {
        this.hashMapToFile = new HashMapToFile(saveFilePath.getPath() + File.separator + "hashmap");
    }

    @Override
    public String read(String key) throws IOException {
        return hashMapToFile.loadValue(key);
    }

    @Override
    public void write(String key, String value) throws IOException {
        hashMapToFile.saveValue(key, value);
    }

    @Override
    public void unset(String key) throws IOException {
        hashMapToFile.deleteValue(key);
    }
}
