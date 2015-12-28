package com.tagmycode.plugin.examples;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.examples.support.*;
import com.tagmycode.sdk.authentication.TagMyCodeApiProduction;
import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;
import support.ResourceGenerate;

import javax.swing.*;

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
        FrameworkConfig frameworkConfig = new FrameworkConfig(
                new PasswordKeyChain(),
                new Preferences(),
                new MessageManager(),
                new TaskFactory(),
                frame);
        Framework framework = new Framework(new TagMyCodeApiProduction(), frameworkConfig, new Secret());

        frame.add(framework.getMainWindow().getMainPanel());

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
