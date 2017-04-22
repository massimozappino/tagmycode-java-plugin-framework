package support;

import com.tagmycode.plugin.AbstractVersion;

public class FakeVersion extends AbstractVersion {

    @Override
    public String getPluginVersion() {
        return "FAKE";
    }
}
