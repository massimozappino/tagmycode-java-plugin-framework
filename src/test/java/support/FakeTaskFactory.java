package support;

import com.tagmycode.plugin.AbstractTaskFactory;

public class FakeTaskFactory extends AbstractTaskFactory {

    @Override
    public void create(Runnable runnable, String title) {
        new Thread(runnable).start();
    }
}
