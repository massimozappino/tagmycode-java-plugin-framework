package support;

import com.tagmycode.plugin.AbstractTaskFactory;

public class FakeTaskFactory extends AbstractTaskFactory {

    @Override
    public void create(Runnable runnable, String title) {
        runnable.run();
    }
}
