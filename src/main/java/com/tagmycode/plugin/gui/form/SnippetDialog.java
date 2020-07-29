package com.tagmycode.plugin.gui.form;


import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.UserPreferences;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.plugin.gui.WindowType;
import com.tagmycode.plugin.gui.field.AbstractFieldValidation;
import com.tagmycode.plugin.gui.field.CodeFieldValidation;
import com.tagmycode.plugin.gui.field.TitleFieldValidation;
import com.tagmycode.plugin.operation.EditSnippetOperation;
import com.tagmycode.sdk.model.DefaultSnippet;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Date;

import static com.tagmycode.plugin.gui.GuiUtil.setPlaceholder;

public class SnippetDialog extends Windowable {
    private static final String NEW_SNIPPET_TITLE = "New snippet";
    private static final String EDIT_SNIPPET_TITLE = "Edit snippet";

    private final SyntaxSnippetEditor codeEditorPane;

    private JPanel contentPane;
    private JTextField tagsTextField;
    private JCheckBox privateSnippetCheckBox;
    private JTextArea descriptionTextField;
    private JTextField titleBox;
    private JComboBox<Language> languageComboBox;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel tagsPanel;
    private JPanel snippetPane;
    private JPanel jpanel;
    private JScrollPane scrollPane;
    private DefaultComboBoxModel<Language> defaultComboBoxModel;
    private EditSnippetOperation createAndEditSnippetOperation;
    private boolean isModified = false;
    private Snippet currentSnippet = new DefaultSnippet();

    public SnippetDialog(final Framework framework, Frame parent) {
        super(framework, parent, WindowType.Type.JFRAME);
        codeEditorPane = framework.getSyntaxSnippetEditorFactory().create();
        snippetPane.add(codeEditorPane.getMainComponent());

        defaultInitWindow();
        initWindow();
    }

    public void setSnippet(Snippet snippet) {
        currentSnippet = snippet;
        populateFieldsWithSnippet(snippet);
        snippetMarkedAsSaved();
        updateWindowTitle();
    }

    protected void updateWindowTitle() {
        String asterisk = isModified ? "*" : "";
        setTitle(isStoredSnippet(currentSnippet) ? asterisk + titleBox.getText() : NEW_SNIPPET_TITLE);
    }

    protected void populateFieldsWithSnippet(Snippet snippet) {
        titleBox.setText(snippet.getTitle());
        descriptionTextField.setText(snippet.getDescription());
        tagsTextField.setText(snippet.getTags());
        codeEditorPane.setTextWithSnippet(snippet);
        privateSnippetCheckBox.setSelected(snippet.isPrivate());
        populateLanguages();
        selectLanguage(snippet.getLanguage());
    }

    @Override
    protected void initWindow() {
        snippetMarkedAsSaved();

        createAndEditSnippetOperation = new EditSnippetOperation(this);
        defaultComboBoxModel = new DefaultComboBoxModel<>();
        languageComboBox.setModel(defaultComboBoxModel);
        descriptionTextField.requestFocus();

        setPlaceholder("Description", descriptionTextField);
        setPlaceholder("tags space separated", tagsTextField);

        int width = framework.getUserPreferences().getInteger(UserPreferences.SNIPPET_DIALOG_WIDTH, 650);
        int height = framework.getUserPreferences().getInteger(UserPreferences.SNIPPET_DIALOG_HEIGHT, 450);
        setSize(width, height);
        setResizable(true);
        setModal(false);

        populateLanguages();
        restorePreferences();

        getWindow().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Component source = (Component) e.getSource();
                Dimension size = source.getSize();
                framework.getUserPreferences().setInteger(UserPreferences.SNIPPET_DIALOG_WIDTH, (int) size.getWidth());
                framework.getUserPreferences().setInteger(UserPreferences.SNIPPET_DIALOG_HEIGHT, (int) size.getHeight());
            }
        });

        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                listenForChanges();
            }
        });
    }

    @Override
    public void closeDialog() {
        if (isModified()) {
            showConfirmDialog();
        } else {
            super.closeDialog();
        }
    }

    protected void showConfirmDialog() {
        String message = String.format("Do you want to save changes to \"%s\"?", titleBox.getText());
        int reply = JOptionPane.showConfirmDialog(getMainComponent(), message, "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);
        switch (reply) {
            case JOptionPane.YES_OPTION:
                onOK();
                break;
            case JOptionPane.NO_OPTION:
                super.closeDialog();
                break;
        }
    }

    @Override
    protected void onOK() {
        if (checkValidForm()) {
            getCreateAndEditSnippetOperation().runWithTask(framework.getTaskFactory(), "Saving snippet");
        }
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

    private void populateLanguages() {
        defaultComboBoxModel.removeAllElements();
        for (Language language : framework.getData().getLanguages()) {
            defaultComboBoxModel.addElement(language);
        }
    }

    private void listenForChanges() {
        DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                snippetMarkedAsModified();
            }

            public void removeUpdate(DocumentEvent e) {
                snippetMarkedAsModified();
            }

            public void insertUpdate(DocumentEvent e) {
                snippetMarkedAsModified();
            }
        };
        titleBox.getDocument().addDocumentListener(listener);
        descriptionTextField.getDocument().addDocumentListener(listener);
        codeEditorPane.getTextArea().getDocument().addDocumentListener(listener);
        tagsTextField.getDocument().addDocumentListener(listener);


        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    snippetMarkedAsModified();
                    saveLastLanguageUsed();
                    changeLanguage(getSelectedLanguage());
                } catch (Exception ignored) {
                }
            }
        });

        privateSnippetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = privateSnippetCheckBox.isSelected();

                try {
                    snippetMarkedAsModified();
                    savePrivateSnippetFlag(selected);
                } catch (TagMyCodeStorageException ignored) {
                }
            }
        });
    }

    protected void changeLanguage(Language language) {
        codeEditorPane.changeLanguage(language);
    }

    private void savePrivateSnippetFlag(boolean selected) throws TagMyCodeStorageException {
        framework.getStorageEngine().savePrivateSnippetFlag(selected);
    }

    private void saveLastLanguageUsed() throws TagMyCodeStorageException {
        framework.getStorageEngine().saveLastLanguageUsed(getSelectedLanguage());
    }

    private Language getSelectedLanguage() {
        return (Language) defaultComboBoxModel.getSelectedItem();
    }

    private void restorePreferences() {
        boolean privateSnippet = framework.getStorageEngine().loadPrivateSnippetFlag();
        privateSnippetCheckBox.setSelected(privateSnippet);
        Language lastLanguageUsed;
        lastLanguageUsed = framework.getStorageEngine().loadLastLanguageUsed();
        defaultComboBoxModel.setSelectedItem(lastLanguageUsed);
    }

    public Snippet createSnippetObject() {
        Snippet snippet = new Snippet();
        snippet.setCode(codeEditorPane.getTextArea().getText());
        snippet.setLanguage((Language) languageComboBox.getSelectedItem());
        snippet.setTitle(titleBox.getText());
        snippet.setDescription(descriptionTextField.getText());
        snippet.setTags(tagsTextField.getText());
        snippet.setPrivate(privateSnippetCheckBox.isSelected());
        Date now = new Date();
        Date creationDate = null;
        if (isStoredSnippet(currentSnippet)) {
            snippet.setLocalId(currentSnippet.getLocalId());
            snippet.setId(currentSnippet.getId());
            creationDate = currentSnippet.getCreationDate();
        }
        snippet.setCreationDate(creationDate == null ? now : creationDate);
        snippet.setUpdateDate(now);
        snippet.setDirty(true);

        return snippet;
    }

    private boolean isStoredSnippet(Snippet currentSnippet) {
        return currentSnippet != null && currentSnippet.getLocalId() != 0;
    }

    public JComboBox<Language> getLanguageComboBox() {
        return languageComboBox;
    }

    public JTextField getTitleBox() {
        return titleBox;
    }

    public SyntaxSnippetEditor getCodeEditorPane() {
        return codeEditorPane;
    }

    public JTextField getTagsTextField() {
        return tagsTextField;
    }

    public JCheckBox getPrivateSnippetCheckBox() {
        return privateSnippetCheckBox;
    }

    public JTextArea getDescriptionTextField() {
        return descriptionTextField;
    }

    public void selectLanguage(Language language) {
        defaultComboBoxModel.setSelectedItem(language);
    }

    public boolean checkValidForm() {
        for (AbstractFieldValidation fieldValidation : createElementValidateList()) {
            if (!fieldValidation.performValidation()) {
                return false;
            }
        }
        return true;
    }

    protected ArrayList<AbstractFieldValidation> createElementValidateList() {
        ArrayList<AbstractFieldValidation> fieldValidations = new ArrayList<AbstractFieldValidation>();

        fieldValidations.add(new TitleFieldValidation(getTitleBox(), framework));
        fieldValidations.add(new CodeFieldValidation(getCodeEditorPane().getTextArea(), framework));

        return fieldValidations;
    }

    public boolean isModified() {
        return isModified;
    }

    public void snippetMarkedAsSaved() {
        isModified = false;
    }

    public void snippetMarkedAsModified() {
        this.isModified = true;
        this.updateWindowTitle();
    }

    public EditSnippetOperation getCreateAndEditSnippetOperation() {
        return createAndEditSnippetOperation;
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
        contentPane.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
        tagsPanel = new JPanel();
        tagsPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(tagsPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Tags:");
        tagsPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tagsTextField = new JTextField();
        tagsPanel.add(tagsTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        privateSnippetCheckBox = new JCheckBox();
        privateSnippetCheckBox.setText("Private snippet");
        tagsPanel.add(privateSnippetCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Title:");
        panel1.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        titleBox = new JTextField();
        titleBox.setText("");
        panel2.add(titleBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Language:");
        panel2.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        languageComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        languageComboBox.setModel(defaultComboBoxModel1);
        panel2.add(languageComboBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel3.add(panel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel4.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel4.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(35);
        splitPane1.setEnabled(true);
        splitPane1.setOrientation(0);
        panel5.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane1.setLeftComponent(scrollPane1);
        descriptionTextField = new JTextArea();
        scrollPane1.setViewportView(descriptionTextField);
        snippetPane = new JPanel();
        snippetPane.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(snippetPane);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
