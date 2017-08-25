package example.support;

import com.tagmycode.plugin.AbstractTaskFactory;
import com.tagmycode.plugin.Framework;

public class TaskFactory extends AbstractTaskFactory {
    @Override
    public void create(final Runnable runnable, final String title) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Framework.LOGGER.info(title + "... START");
                runnable.run();
                Framework.LOGGER.info(title + "... END");
            }
        });
        thread.start();
    }
}
