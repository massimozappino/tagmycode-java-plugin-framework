package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.FilterSnippetsTextField;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.table.SnippetsTable;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class QuickSearchDialog extends AbstractDialog {
    private final JButton buttonOk;
    private final JButton buttonCancel;
    private final JTable jtable;
    private FilterSnippetsTextField quickFilterSnippetsTextField;
    private JPanel mainPanel;
    private JPanel resultsPanel;
    private IDocumentInsertText documentInsertText;
    private SnippetsTable snippetsTable;

    public QuickSearchDialog(final Framework framework, Frame parent) {
        super(framework, parent);
        buttonOk = new JButton();
        buttonCancel = new JButton();

        KeyListener insertCodeKeyListener = createInsertIntoDocumentKeyListener();
        quickFilterSnippetsTextField.addKeyListener(insertCodeKeyListener);

        jtable = snippetsTable.getSnippetsComponent();
        jtable.setTableHeader(null);

        resultsPanel.add(snippetsTable.getMainComponent());

        defaultInitWindow();
        initWindow();
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
                    insertCodeIntoDocument(getSelectedSnippet());
                    hideDialog();
                }
            }
        };
    }

    private void insertCodeIntoDocument(Snippet snippet) {
        if (documentInsertText != null) {
            documentInsertText.insertText(snippet.getCode());
        }
    }

    public Snippet getSelectedSnippet() {
        return snippetsTable.getSelectedSnippet();
    }

    @Override
    protected void initWindow() {
        getDialog().getRootPane().setDefaultButton(null);
        getDialog().setSize(400, 300);
        getDialog().setResizable(true);
        getDialog().setUndecorated(true);
        getDialog().setModal(false);
        getDialog().addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) {
                hideDialog();
            }

            public void windowGainedFocus(WindowEvent e) {
            }
        });
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
    }

    private void createUIComponents() {
        snippetsTable = new SnippetsTable(framework);
        quickFilterSnippetsTextField = new FilterSnippetsTextField(framework, getSnippetsTable());
        quickFilterSnippetsTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_UP: {
                        cycleTableSelectionRows("up");
                        break;
                    }

                    case KeyEvent.VK_DOWN: {
                        cycleTableSelectionRows("down");
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void cycleTableSelectionRows(String direction) {
        int size = jtable.getRowCount();
        if (size > 0) {
            int selectedIndex = jtable.getSelectedRow();
            int newSelectionIndex = 0;

            if (direction.equals("up")) {
                newSelectionIndex = size - 1;
                if (selectedIndex > 0) {
                    newSelectionIndex = selectedIndex - 1;
                }
            } else {
                if (selectedIndex != size - 1) {
                    newSelectionIndex = selectedIndex + 1;
                }
            }
            selectTableRow(newSelectionIndex);
        }
    }

    private void selectTableRow(int newSelectionIndex) {
        jtable.setRowSelectionInterval(newSelectionIndex, newSelectionIndex);
        jtable.scrollRectToVisible(new Rectangle(jtable.getCellRect(newSelectionIndex, 0, true)));
    }

    @Override
    public void display() {
        super.display();
        quickFilterSnippetsTextField.selectAll();
        quickFilterSnippetsTextField.requestFocus();
        quickFilterSnippetsTextField.doFilter();
    }

    public SnippetsTable getSnippetsTable() {
        return snippetsTable;
    }
}