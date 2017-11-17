package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.BackgroundWorker;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.MD5Util;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.ThemeItem;
import com.tagmycode.sdk.model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import static com.tagmycode.plugin.gui.GuiUtil.addClickableLink;
import static com.tagmycode.plugin.gui.GuiUtil.setBold;

public class SettingsForm extends Windowable {
    protected JLabel email;
    protected JLabel userName;
    private JPanel mainPanel;
    private Framework framework;
    private JButton logoutButton;
    private JLabel profilePicture;
    private JButton closeButton;
    private JComboBox themeComboBox;
    private JComboBox fontSizeComboBox;

    public SettingsForm(final Framework framework, Frame parent) {
        super(framework, parent);
        this.framework = framework;
        setTitle("Settings");
        defaultInitWindow();
        setBold(userName);
        addClickableLink(framework, profilePicture, "https://tagmycode.com/account");
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
        getRootPane().setDefaultButton(null);
        setMinimumSize(new Dimension(450, 300));
        setResizable(true);

        initGeneralTab();
        initAccountTab();
    }

    private void initAccountTab() {
        new BackgroundWorker(new Runnable() {
            @Override
            public void run() {
                loadProfilePicture(framework.getData().getAccount().getEmail());
            }
        }).execute();
    }

    private void initGeneralTab() {
        configureThemesComboBox();
        configureFontSizeComboBox();
        ActionListener actionListener = createEditorActionListener();
        themeComboBox.addActionListener(actionListener);
        fontSizeComboBox.addActionListener(actionListener);
    }

    private void configureFontSizeComboBox() {
        fontSizeComboBox.addItem(8);
        fontSizeComboBox.addItem(9);
        fontSizeComboBox.addItem(10);
        fontSizeComboBox.addItem(12);
        fontSizeComboBox.addItem(13);
        fontSizeComboBox.addItem(14);
        fontSizeComboBox.addItem(16);
        fontSizeComboBox.addItem(18);
        fontSizeComboBox.addItem(24);
        fontSizeComboBox.addItem(36);
        fontSizeComboBox.addItem(48);
        fontSizeComboBox.setSelectedItem(framework.getSyntaxSnippetEditorFactory().getFontSize());
    }

    private ActionListener createEditorActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String themeFile = ((ThemeItem) themeComboBox.getSelectedItem()).getKey();
                try {
                    int editorFontSize = (int) fontSizeComboBox.getSelectedItem();
                    framework.getStorageEngine().saveEditorTheme(themeFile);
                    framework.getStorageEngine().saveEditorFontSize(editorFontSize);
                    framework.getSyntaxSnippetEditorFactory().applyChanges(themeFile, editorFontSize);
                } catch (TagMyCodeStorageException e) {
                    framework.logError(e);
                }
            }
        };
    }

    private void configureThemesComboBox() {
        ArrayList<ThemeItem> themes = framework.getSyntaxSnippetEditorFactory().createThemeArray();
        ThemeItem loadedTheme = loadTheme(themes);

        for (ThemeItem theme : themes) {
            themeComboBox.addItem(theme);
        }
        if (loadedTheme != null) {
            themeComboBox.setSelectedItem(loadedTheme);
        }
    }

    private ThemeItem loadTheme(ArrayList<ThemeItem> themes) {
        ThemeItem loadedTheme = null;
        try {
            String loadedThemeFile = framework.getStorageEngine().loadEditorTheme();
            for (ThemeItem theme : themes) {
                if (theme.getKey().equals(loadedThemeFile)) {
                    loadedTheme = theme;
                }
            }
        } catch (TagMyCodeStorageException e) {
            framework.logError(e);
        }
        return loadedTheme;
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

