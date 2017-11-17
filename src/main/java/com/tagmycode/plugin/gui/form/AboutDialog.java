package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractVersion;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialog extends Windowable {
    private JComponent contentPane;
    private JButton buttonCancel;
    private JButton licenseButton;
    private JButton reportAnIssueButton;
    private JLabel tagmycodeLinkLabel;
    private JLabel pluginVersionLabel;
    private JLabel frameworkBuildDateLabel;
    private JLabel frameworkVersionLabel;
    private JLabel title;
    private JButton buttonOK = new JButton();

    public AboutDialog(Framework framework, Frame parent) {
        super(framework, parent);

        configureButtons();
        GuiUtil.addClickableLink(framework, tagmycodeLinkLabel, "https://tagmycode.com");
        AbstractVersion version = framework.getVersion();

        if (version.getPluginTitle().length() > 0) {
            title.setText(version.getPluginTitle());
        }
        appendTextToJLabel(pluginVersionLabel, version.getPluginVersion());
        appendTextToJLabel(frameworkVersionLabel, version.getFrameworkVersion());
        appendTextToJLabel(frameworkBuildDateLabel, version.getFrameworkBuildDate());

        defaultInitWindow();
        initWindow();
    }

    private void appendTextToJLabel(JLabel jLabel, String text) {
        jLabel.setText(jLabel.getText() + " " + text);
    }

    private void configureButtons() {
        licenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.openUrlInBrowser("http://www.apache.org/licenses/LICENSE-2.0");
            }
        });
        reportAnIssueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.openUrlInBrowser("https://tagmycode.com/about/contact");
            }
        });
    }

    @Override
    protected void initWindow() {
        setSize(400, 300);
        setResizable(false);
        setTitle("About TagMyCode");
        setUndecorated(true);
        hideOnFocusLost();
    }

    @Override
    public JButton getButtonOk() {
        return buttonOK;
    }

    @Override
    protected JButton getButtonCancel() {
        return buttonCancel;
    }

    @Override
    public JComponent getMainComponent() {
        return contentPane;
    }

    @Override
    protected void onOK() {

    }
}
