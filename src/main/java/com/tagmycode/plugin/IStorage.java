package com.tagmycode.plugin;

import java.io.IOException;

public interface IStorage {

    String read(String key) throws IOException;

    void write(String key, String value) throws IOException;

    void unset(String key) throws IOException;
}
