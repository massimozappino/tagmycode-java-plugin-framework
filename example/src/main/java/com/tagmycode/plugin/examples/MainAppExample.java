package com.tagmycode.plugin.examples;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.examples.support.*;
import com.tagmycode.sdk.authentication.TagMyCodeApiProduction;

import javax.swing.*;
import java.awt.*;

public class MainAppExample {
    public static void main(String args[]) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        JFrame frame = new JFrame("TagMyCode Plugin Example");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setBounds(200, 200, 600, 200);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        FrameworkConfig frameworkConfig = new FrameworkConfig(
                new PasswordKeyChain(),
                new Storage(),
                new MessageManager(),
                new TaskFactory(),
                frame);
        final Framework framework = new Framework(new TagMyCodeApiProduction(), frameworkConfig, new Secret());

        contentPane.add(framework.getMainWindow().getMainComponent(), BorderLayout.CENTER);

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
