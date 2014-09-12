package support;

import com.tagmycode.plugin.AbstractSecret;

public class FakeSecret extends AbstractSecret {

    @Override
    public String getConsumerId() {
        return "ID";
    }

    @Override
    public String getConsumerSecret() {
        return "SECRET";
    }
}
