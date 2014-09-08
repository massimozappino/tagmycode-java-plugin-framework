package support;


import com.tagmycode.plugin.AbstractPreferences;

import java.util.HashMap;

public class FakePreferences extends AbstractPreferences {
    private HashMap<String, String> resource;

    public FakePreferences() {
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
