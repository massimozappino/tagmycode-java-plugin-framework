package com.tagmycode.plugin.gui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
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
    private JTextField userDirectoryTextField;
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
        userDirectoryTextField.setText(framework.getSaveFilePath().getPath());
        GuiUtil.makeTransparentTextField(userDirectoryTextField);

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
        setSize(480, 350);
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
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(8, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Close");
        panel2.add(buttonCancel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        licenseButton = new JButton();
        licenseButton.setText("License");
        panel1.add(licenseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reportAnIssueButton = new JButton();
        reportAnIssueButton.setText("Report an issue");
        panel1.add(reportAnIssueButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/images/tagmycode_logo.png")));
        label1.setText("");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("fill:d:grow", "center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,top:4dlu:noGrow"));
        contentPane.add(panel4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pluginVersionLabel = new JLabel();
        pluginVersionLabel.setText("Plugin version:");
        CellConstraints cc = new CellConstraints();
        panel4.add(pluginVersionLabel, cc.xy(1, 3));
        frameworkBuildDateLabel = new JLabel();
        frameworkBuildDateLabel.setText("Framework build date:");
        panel4.add(frameworkBuildDateLabel, cc.xy(1, 9));
        title = new JLabel();
        Font titleFont = this.$$$getFont$$$(null, Font.BOLD, 16, title.getFont());
        if (titleFont != null) title.setFont(titleFont);
        title.setText("TagMyCode Plugin");
        panel4.add(title, cc.xy(1, 1));
        frameworkVersionLabel = new JLabel();
        frameworkVersionLabel.setText("Framework version:");
        panel4.add(frameworkVersionLabel, cc.xy(1, 7));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, cc.xy(1, 15, CellConstraints.DEFAULT, CellConstraints.FILL));
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        panel4.add(toolBar$Separator1, cc.xy(1, 5));
        final JLabel label2 = new JLabel();
        label2.setText("User directory:");
        panel4.add(label2, cc.xy(1, 11));
        userDirectoryTextField = new JTextField();
        userDirectoryTextField.setEditable(false);
        panel4.add(userDirectoryTextField, cc.xy(1, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        contentPane.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 30), null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel5.add(separator1, BorderLayout.CENTER);
        tagmycodeLinkLabel = new JLabel();
        tagmycodeLinkLabel.setHorizontalAlignment(0);
        tagmycodeLinkLabel.setText("<html><a href=\"\">www.tagmycode.com</a>");
        contentPane.add(tagmycodeLinkLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        contentPane.add(spacer3, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
