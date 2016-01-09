package com.tagmycode.plugin;

import java.io.IOException;

public interface IStorage {

    abstract String read(String key) throws IOException;

    abstract void write(String key, String value) throws IOException;

    abstract void unset(String key) throws IOException;
}
