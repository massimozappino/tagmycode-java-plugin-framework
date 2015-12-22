package com.tagmycode.plugin.example;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;

import javax.swing.*;


public class Controller {

    private final Framework framework;

    public Controller() {
        FrameworkConfig frameworkConfig = new FrameworkConfig(
                new PasswordKeyChain(),
                ,
                new FakeMessageManager(),
                new FakeTaskFactory(),
                null);
        this.framework = new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new Secret());
    }

    public void launch() {
        JFrame frame = new JFrame("FrameDemo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();

        frame.setVisible(true);
    }
}
