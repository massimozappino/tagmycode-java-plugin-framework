package support;

import com.tagmycode.plugin.IConsole;

public class FakeConsole implements IConsole {
    private String fullLog = "";

    @Override
    public void log(String message) {
        fullLog += message;
    }

    @Override
    public String getFullLog() {
        return fullLog;
    }
}
