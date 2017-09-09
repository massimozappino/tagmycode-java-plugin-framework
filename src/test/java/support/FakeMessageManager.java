package support;

import com.tagmycode.plugin.IMessageManager;

public class FakeMessageManager implements IMessageManager {

    @Override
    public void errorLog(String message) {
        System.err.println("ERROR LOG: " + message);
    }

    @Override
    public void errorDialog(String message) {
        System.err.println("ERROR DIALOG: " + message);
    }
}
