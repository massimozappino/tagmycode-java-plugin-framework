package support;

import com.tagmycode.plugin.GuiThread;

public class FakeGuiThread extends GuiThread {
    public static boolean hasRan = false;

    public boolean hasRan() {
        return hasRan;
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
