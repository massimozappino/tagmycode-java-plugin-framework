package support;


import com.tagmycode.plugin.AbstractStorage;

import java.util.HashMap;

public class FakeStorage extends AbstractStorage {
    private HashMap<String, String> resource;

    public FakeStorage() {
        resource = new HashMap<String, String>();
    }

    public void generateExceptionForLanguageCollection() {
        write("languages", "{");
    }

    @Override
    protected void write(String key, String value) {
        resource.put(key, value);
    }

    @Override
    protected String read(String key) {
        return resource.get(key);
    }

    @Override
    protected void unset(String key) {
        resource.remove(key);
    }
}
