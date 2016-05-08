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
//                        cycleListSelectionUp();
                        break;
                    }

                    case KeyEvent.VK_DOWN: {
//                        cycleListSelectionDown();
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

//    private void cycleListSelectionDown() {
//        ListModel<Snippet> listModel = list1.getModel();
//
//        if (listModel.getSize() > 0) {
//            int selectedIndex = list1.getSelectedIndex();
//
//            int newSelectionIndex = 0;
//            if (selectedIndex != listModel.getSize() - 1) {
//                newSelectionIndex = selectedIndex + 1;
//            }
//            selectListIndex(newSelectionIndex);
//        }
//    }
//
//    private void cycleListSelectionUp() {
//        ListModel<Snippet> listModel = list1.getModel();
//
//        if (listModel.getSize() > 0) {
//            int selectedIndex = list1.getSelectedIndex();
//            int newSelectionIndex = listModel.getSize() - 1;
//            if (selectedIndex > 0) {
//                newSelectionIndex = selectedIndex - 1;
//            }
//            selectListIndex(newSelectionIndex);
//        }
//    }
//
//    private void selectListIndex(int newSelectionIndex) {
//        list1.setSelectedIndex(newSelectionIndex);
//        list1.ensureIndexIsVisible(newSelectionIndex);
//    }

    @Override
    public void display() {
        super.display();
        quickFilterSnippetsTextField.selectAll();
        quickFilterSnippetsTextField.requestFocus();
        //TODO
        snippetsTable.fireSnippetsChanged();
    }

    public SnippetsTable getSnippetsTable() {
        return snippetsTable;
    }
}