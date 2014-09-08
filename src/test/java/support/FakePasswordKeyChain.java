package support;


import com.tagmycode.plugin.IPasswordKeyChain;

import java.util.HashMap;

public class FakePasswordKeyChain implements IPasswordKeyChain {

    private final HashMap<String, String> data;

    public FakePasswordKeyChain() {
        data = new HashMap<String, String>();
    }

    @Override
    public void saveValue(String key, String value) {
        data.put(key, value);
    }

    @Override
    public String loadValue(String key) {
        return data.get(key);
    }

    @Override
    public void deleteValue(String key) {
        data.remove(key);
    }
}
