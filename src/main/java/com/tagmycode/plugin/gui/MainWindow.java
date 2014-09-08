package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.Console;
import com.tagmycode.plugin.Framework;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.BorderFactory.createEmptyBorder;

public class MainWindow {
    private final Console console;
    private JEditorPane editorPane;
    private JButton leaveFeedbackButton;
    private JLabel tagMyCodeLabel;
    private JPanel mainPanel;
    private JScrollPane scrollPane1;
    private final Framework framework;

    public MainWindow(final Framework framework) {
        this.framework = framework;
        scrollPane1.setBorder(createEmptyBorder());
        editorPane.setBorder(createEmptyBorder());
        editorPane.setEditable(false);
        console = new Console(editorPane);
        leaveFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Browser().openUrl("http://tagmycode.com/about/contact");
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Console getConsole() {
        return console;
    }
}
