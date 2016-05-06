package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.MD5Util;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.sdk.model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

import static com.tagmycode.plugin.gui.GuiUtil.addClickableLink;
import static com.tagmycode.plugin.gui.GuiUtil.setBold;

public class SettingsForm extends AbstractDialog {
    protected JLabel email;
    protected JLabel userName;
    private JPanel mainPanel;
    private Framework framework;
    private JButton logoutButton;
    private JLabel profilePicture;
    private JButton closeButton;

    public SettingsForm(final Framework framework, Frame parent) {
        super(framework, parent);
        this.framework = framework;

        defaultInitWindow();
        setBold(userName);
        addClickableLink(profilePicture, "https://tagmycode.com/account");
    }

    private void fillData() {
        User account = framework.getData().getAccount();
        email.setText(account.getEmail());
        userName.setText(account.getUsername());
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    @Override
    protected void initWindow() {
        getDialog().getRootPane().setDefaultButton(null);
        getDialog().setSize(450, 300);
        getDialog().setResizable(true);

        new BackgroundWorker(new Runnable() {
            @Override
            public void run() {
                loadProfilePicture(framework.getData().getAccount().getEmail());
            }
        }).execute();
    }

    @Override
    protected void onOK() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to log out?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            framework.logout();
            closeDialog();
        }
    }

    @Override
    public JButton getButtonOk() {
        return logoutButton;
    }

    @Override
    protected JButton getButtonCancel() {
        return closeButton;
    }

    @Override
    public void display() {
        initWindow();
        super.display();
        fillData();
    }

    private void loadProfilePicture(final String email) {
        try {
            String md5Email = MD5Util.md5Hex(email);
            String path = "https://www.gravatar.com/avatar/" + md5Email + "?s=128&d=wavatar&r=pg";
            URL url = new URL(path);
            BufferedImage image = ImageIO.read(url);
            profilePicture.setIcon(new ImageIcon(image));
        } catch (Exception ignore) {
        }
    }
}

class BackgroundWorker extends SwingWorker<Integer, String> {
    private Runnable runnable;

    public BackgroundWorker(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        runnable.run();
        return 0;
    }

}