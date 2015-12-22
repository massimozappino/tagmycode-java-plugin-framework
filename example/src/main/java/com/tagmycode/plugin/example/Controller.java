package com.tagmycode.plugin.example;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.gui.SettingsForm;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;

import javax.swing.*;


public class Controller {

    private final Framework framework;
    private final JFrame frame;
    public Controller() {
        frame = new JFrame("TagMyCode Plugin Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FrameworkConfig frameworkConfig = new FrameworkConfig(
                new PasswordKeyChain(),
                new Preferences(),
                new MessageManager(),
                new TaskFactory(),
                frame);
        this.framework = new Framework(new TagMyCodeApiDevelopment(), frameworkConfig, new Secret());


        frame.add(new SettingsForm(framework).getMainPanel());
    }

    public void launch() {
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
