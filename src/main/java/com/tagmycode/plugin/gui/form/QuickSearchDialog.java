package com.tagmycode.plugin.gui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.filter.FilterSnippetsTextField;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.MoveUpDownFilterFieldKeyListener;
import com.tagmycode.plugin.gui.SyntaxSnippetEditor;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;

public class QuickSearchDialog extends Windowable {
    private final JButton buttonOk;
    private final JPanel previewPanel;
    private JButton buttonCancel;
    private JTable jTable;
    private FilterSnippetsTextField filterSnippetsTextField;
    private JPanel mainPanel;
    private JPanel resultsPanel;
    private JPanel snippetViewPanel;
    private JPanel centerPanel;
    private JButton insertAtCursorButton;
    private JButton openButton;
    private JPanel filterPanel;
    private IDocumentInsertText documentInsertText;
    private SnippetsTable snippetsTable;
    private SnippetView snippetView;

    public QuickSearchDialog(final Framework framework, Frame parent) {
        super(framework, parent);
        snippetView = new SnippetView(framework.getSyntaxSnippetEditorFactory());
        snippetsTable = new SnippetsTable(framework);

        buttonOk = new JButton();
        insertAtCursorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertCodeIntoDocument();
            }
        });
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSnippet();
            }
        });

        disableSnippetButtons();

        configureJTable();

        snippetViewPanel.setPreferredSize(new Dimension(-1, 130));

        previewPanel = new JPanel(new GridBagLayout());
        previewPanel.add(new JLabel("Select a snippet to preview"));
        snippetViewPanel.removeAll();
        snippetViewPanel.add(previewPanel);

        filterSnippetsTextField = new FilterSnippetsTextField(snippetsTable.getFilterSnippetsOperation());
        filterSnippetsTextField.addKeyListener(new MoveUpDownFilterFieldKeyListener(jTable));
        filterSnippetsTextField.addKeyListener(createInsertIntoDocumentKeyListener());
        filterPanel.add(filterSnippetsTextField);

        snippetsTable.getCellSelectionModel().addListSelectionListener(createSelectionListener());

        resultsPanel.add(snippetsTable.getMainComponent());

        defaultInitWindow();
        initWindow();
    }

    private void configureJTable() {
        jTable = snippetsTable.getJTable();
        jTable.setTableHeader(null);
        TableColumnModel columnModel = jTable.getColumnModel();
        jTable.removeColumn(columnModel.getColumn(SnippetsTableModel.MODIFIED));
        jTable.removeColumn(columnModel.getColumn(SnippetsTableModel.IS_PRIVATE));
        jTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSnippet();
                }
            }
        });
        jTable.addKeyListener(new KeyAdapter() {
                                  @Override
                                  public void keyPressed(KeyEvent e) {
                                      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                          insertCodeIntoDocument();
                                      }
                                  }
                              }
        );
    }

    private void editSnippet() {
        framework.showSnippetDialog(snippetsTable.getSelectedSnippet());
        hideDialog();
    }

    private ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    snippetViewPanel.removeAll();
                    disableSnippetButtons();

                    Snippet snippet = snippetsTable.getSelectedSnippet();

                    if (snippet != null) {
                        snippetView.setSnippet(snippet);
                        SyntaxSnippetEditor snippetEditorPane = snippetView.getSnippetEditorPane();
                        snippetEditorPane.setPreview();
                        snippetViewPanel.add(snippetEditorPane.getMainComponent());
                        enableSnippetButtons();
                    } else {
                        snippetViewPanel.add(previewPanel);
                    }
                    snippetViewPanel.revalidate();
                    snippetViewPanel.repaint();
                }
            }
        };
    }

    private void disableSnippetButtons() {
        openButton.setEnabled(false);
        insertAtCursorButton.setEnabled(false);
    }

    private void enableSnippetButtons() {
        openButton.setEnabled(true);
        insertAtCursorButton.setEnabled(true);
    }

    private KeyListener createInsertIntoDocumentKeyListener() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    insertCodeIntoDocument();
                    hideDialog();
                }
            }
        };
    }

    private void insertCodeIntoDocument() {
        if (documentInsertText != null) {
            Snippet snippet = getSelectedSnippet();
            if (snippet != null) {
                documentInsertText.insertText(snippet.getCode());
            }
            hideDialog();
        }
    }

    public Snippet getSelectedSnippet() {
        return snippetsTable.getSelectedSnippet();
    }

    @Override
    protected void initWindow() {
        setTitle("Quick search");
        getRootPane().setDefaultButton(null);
        setSize(400, 300);
        setResizable(true);
        hideOnFocusLost();
    }


    @Override
    protected void onOK() {
    }

    @Override
    protected void onCancel() {
        hideDialog();
    }

    @Override
    public JButton getButtonOk() {
        return buttonOk;
    }

    @Override
    protected JButton getButtonCancel() {
        return buttonCancel;
    }

    @Override
    public JComponent getMainComponent() {
        return mainPanel;
    }

    public void setDocumentInsertText(IDocumentInsertText documentInsertText) {
        this.documentInsertText = documentInsertText;
        if (documentInsertText == null) {
            insertAtCursorButton.setVisible(false);
        } else {
            insertAtCursorButton.setVisible(true);
        }
    }

    @Override
    public void display() {
        super.display();
        filterSnippetsTextField.selectAll();
        filterSnippetsTextField.requestFocus();
        filterSnippetsTextField.doFilter();
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
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, BorderLayout.EAST);
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel3.add(buttonCancel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        insertAtCursorButton = new JButton();
        insertAtCursorButton.setText("Insert at cursor");
        panel3.add(insertAtCursorButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        panel3.add(openButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        mainPanel.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        filterPanel = new JPanel();
        filterPanel.setLayout(new BorderLayout(0, 0));
        panel4.add(filterPanel, BorderLayout.CENTER);
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.add(centerPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(115);
        splitPane1.setOrientation(0);
        centerPanel.add(splitPane1, BorderLayout.CENTER);
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout(0, 0));
        splitPane1.setLeftComponent(resultsPanel);
        snippetViewPanel = new JPanel();
        snippetViewPanel.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(snippetViewPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}