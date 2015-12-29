package com.tagmycode.plugin.examples;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.examples.support.*;
import com.tagmycode.sdk.authentication.TagMyCodeApiProduction;
import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;
import support.ResourceGenerate;

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
        frame.setTitle("Status bar simulator");
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

//        StatusBar statusBar = new StatusBar();
//        contentPane.add(statusBar, BorderLayout.SOUTH);


        FrameworkConfig frameworkConfig = new FrameworkConfig(
                new PasswordKeyChain(),
                new Preferences(),
                new MessageManager(),
                new TaskFactory(),
                frame);
        Framework framework = new Framework(new TagMyCodeApiProduction(), frameworkConfig, new Secret());

        contentPane.add(framework.getMainWindow().getMainPanel(), BorderLayout.CENTER);

        try {
            ModelCollection<Snippet> snippets = new ResourceGenerate().aSnippetCollection();
            framework.getMainWindow().getSnippetsTab().getSnippetsJList().updateWithSnippets(snippets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
