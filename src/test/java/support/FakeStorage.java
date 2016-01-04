package support;


import com.tagmycode.plugin.IStorage;

import java.util.HashMap;

public class FakeStorage implements IStorage {
    private HashMap<String, String> resource;

    public FakeStorage() {
        resource = new HashMap<String, String>();
    }

    public void generateExceptionForLanguageCollection() {
        write("languages", "{");
    }

    @Override
    public void write(String key, String value) {
        resource.put(key, value);
    }

    @Override
    public String read(String key) {
        return resource.get(key);
    }

    @Override
    public void unset(String key) {
        resource.remove(key);
    }
}
