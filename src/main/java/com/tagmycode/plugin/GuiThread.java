package com.tagmycode.plugin;

import javax.swing.*;

public class GuiThread {

    public void execute(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

}
