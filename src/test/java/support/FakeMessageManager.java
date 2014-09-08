package support;

import com.tagmycode.plugin.IMessageManager;

public class FakeMessageManager implements IMessageManager {

    private String currentMessage;

    @Override
    public void error(String message) {
        System.err.println("ERROR: " + message);
        currentMessage = message;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }
}
