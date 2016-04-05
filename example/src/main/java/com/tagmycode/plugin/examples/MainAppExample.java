package com.tagmycode.plugin.examples;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.examples.support.MessageManager;
import com.tagmycode.plugin.examples.support.PasswordKeyChain;
import com.tagmycode.plugin.examples.support.Storage;
import com.tagmycode.plugin.examples.support.TaskFactory;
import com.tagmycode.sdk.authentication.TagMyCodeApiProduction;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainAppExample {
    public static void main(String args[]) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        JFrame frame = new JFrame("TagMyCode Plugin Example");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setBounds(200, 200, 600, 200);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel lookAndFeelPanel = getLookAndFeelPanel(frame);
        JPanel comp = new JPanel(new BorderLayout());
        contentPane.add(comp, BorderLayout.NORTH);

        comp.add(lookAndFeelPanel, BorderLayout.WEST);
        final JTextField lastSnippetsUpdateTextField = new JTextField("", 20);
        comp.add(lastSnippetsUpdateTextField, BorderLayout.EAST);
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
                framework.showSearchDialog(null);
            }
        });
        lastSnippetsUpdateTextField.setText(framework.getData().getLastSnippetsUpdate());
        lastSnippetsUpdateTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doFilter();
            }

            private void doFilter() {
                framework.getData().setLastSnippetsUpdate(lastSnippetsUpdateTextField.getText());
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
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
