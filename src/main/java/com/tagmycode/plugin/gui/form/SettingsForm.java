package com.tagmycode.plugin.gui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.tagmycode.plugin.BackgroundWorker;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.MD5Util;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.ThemeItem;
import com.tagmycode.sdk.model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static com.tagmycode.plugin.gui.GuiUtil.addClickableLink;
import static com.tagmycode.plugin.gui.GuiUtil.setBold;

public class SettingsForm extends Windowable {
    protected JLabel email;
    protected JLabel userName;
    private JPanel mainPanel;
    private final Framework framework;
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        closeButton = new JButton();
        closeButton.setText("Close");
        panel2.add(closeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        mainPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("General", panel3);
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel3.add(panel4, BorderLayout.CENTER);
        final JLabel label1 = new JLabel();
        label1.setText("Editor theme:");
        CellConstraints cc = new CellConstraints();
        panel4.add(label1, cc.xy(1, 1));
        themeComboBox = new JComboBox();
        panel4.add(themeComboBox, cc.xy(3, 1));
        final JLabel label2 = new JLabel();
        label2.setText("Editor font size:");
        panel4.add(label2, cc.xy(1, 3));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:d:grow"));
        panel4.add(panel5, cc.xy(3, 3));
        fontSizeComboBox = new JComboBox();
        panel5.add(fontSizeComboBox, cc.xy(1, 1));
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Account", panel6);
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        logoutButton = new JButton();
        logoutButton.setIcon(new ImageIcon(getClass().getResource("/icons/signout.png")));
        logoutButton.setText("Logout");
        panel8.add(logoutButton, BorderLayout.SOUTH);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 5, 0), -1, -1));
        panel8.add(panel9, BorderLayout.NORTH);
        profilePicture = new JLabel();
        profilePicture.setIcon(new ImageIcon(getClass().getResource("/images/profile.jpg")));
        profilePicture.setText("");
        panel9.add(profilePicture, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new FormLayout("fill:d:grow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel7.add(panel10, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        userName = new JLabel();
        Font userNameFont = this.$$$getFont$$$(null, -1, -1, userName.getFont());
        if (userNameFont != null) userName.setFont(userNameFont);
        userName.setText("Label");
        panel10.add(userName, cc.xy(1, 1));
        email = new JLabel();
        email.setText("Label");
        panel10.add(email, cc.xy(1, 3));
        final Spacer spacer3 = new Spacer();
        panel7.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}

