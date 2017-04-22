package example.support;

import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.IMessageManager;

import javax.swing.*;

public class MessageManager implements IMessageManager {
    @Override
    public void error(final String message) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message, "TagMyCode Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        System.err.println(message);
    }
}
