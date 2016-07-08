package com.tagmycode.plugin.examples;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.examples.support.MessageManager;
import com.tagmycode.plugin.examples.support.PasswordKeyChain;
import com.tagmycode.plugin.examples.support.Storage;
import com.tagmycode.plugin.examples.support.TaskFactory;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.sdk.authentication.TagMyCodeApiProduction;
import com.tagmycode.sdk.exception.TagMyCodeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainAppExample {
    public static void main(String args[]) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException, TagMyCodeException {
        JFrame frame = new JFrame("TagMyCode Plugin Example");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setBounds(200, 200, 600, 200);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel lookAndFeelPanel = getLookAndFeelPanel(frame);
        JPanel comp = new JPanel(new BorderLayout());
        contentPane.add(comp, BorderLayout.NORTH);

        comp.add(lookAndFeelPanel, BorderLayout.WEST);
        JButton searchButton = new JButton("Search");
        comp.add(searchButton, BorderLayout.CENTER);


        FrameworkConfig frameworkConfig = new FrameworkConfig(
                new PasswordKeyChain(),
                new Storage(),
                new MessageManager(),
                new TaskFactory(),
                frame);
        final Framework framework = new Framework(new TagMyCodeApiProduction(), frameworkConfig, new Secret());
        framework.start();
        contentPane.add(framework.getMainFrame(), BorderLayout.CENTER);


        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.showSearchDialog(new IDocumentInsertText() {
                    @Override
                    public void insertText(String text) {
                        System.out.println(text);
                    }
                });
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    framework.closeFramework();
                } catch (TagMyCodeStorageException e) {
                    e.printStackTrace();
                }
            }
        }, "Shutdown-thread"));
    }

    private static JPanel getLookAndFeelPanel(final Frame frame) {
        JPanel lookAndFeelPanel = new JPanel();
        final JComboBox<String> jComboBox = new JComboBox<>();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            jComboBox.addItem(info.getClassName());
        }
        lookAndFeelPanel.add(jComboBox);

        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) jComboBox.getSelectedItem();
                try {
                    UIManager.setLookAndFeel(selectedItem);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                SwingUtilities.updateComponentTreeUI(frame);

            }
        });
        return lookAndFeelPanel;
    }
}
