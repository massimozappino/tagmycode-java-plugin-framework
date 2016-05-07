package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.QuickFilterSnippetsTextField;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class QuickSearchDialog extends AbstractDialog {
    private final JButton buttonOk;
    private final JButton buttonCancel;
    private QuickFilterSnippetsTextField quickFilterSnippetsTextField;
    private JPanel mainPanel;
    private JScrollPane scroll;
    private JList<Snippet> list1;
    private IDocumentInsertText documentInsertText;
    private DefaultListModel<Snippet> model;

    public QuickSearchDialog(final Framework framework, Frame parent) {
        super(framework, parent);
        buttonOk = new JButton();
        buttonCancel = new JButton();
        model = new DefaultListModel<>();
        list1.setModel(model);
        list1.setCellRenderer(new SnippetRenderer());

        list1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                Snippet snippet = getSelectedSnippet();

                if (evt.getClickCount() == 2) {
                    framework.openSnippet(snippet);
                }
            }
        });
        list1.addKeyListener(new KeyListener() {
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
                }
            }
        });

        defaultInitWindow();
        initWindow();
    }

    private void insertCodeIntoDocument(Snippet snippet) {
        if (documentInsertText != null) {
            documentInsertText.insertText(snippet.getCode());
        }
    }

    public Snippet getSelectedSnippet() {
        Snippet selectedSnippet = null;
        if (!list1.isSelectionEmpty()) {
            selectedSnippet = model.getElementAt(list1.getSelectedIndex());
        }

        return selectedSnippet;
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
        quickFilterSnippetsTextField.setDocumentInsertText(documentInsertText);
    }

    public void populateResults(final SnippetCollection filteredSnippets) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                model.clear();
                for (Snippet snippet : filteredSnippets) {
                    model.addElement(snippet);
                }

            }
        });
    }

    private void createUIComponents() {
        quickFilterSnippetsTextField = new QuickFilterSnippetsTextField(this);
        quickFilterSnippetsTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_UP: {
                        cycleListSelectionUp();
                        break;
                    }

                    case KeyEvent.VK_DOWN: {
                        cycleListSelectionDown();
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void cycleListSelectionDown() {
        ListModel<Snippet> listModel = list1.getModel();

        if (listModel.getSize() > 0) {
            int selectedIndex = list1.getSelectedIndex();

            int newSelectionIndex = 0;
            if (selectedIndex != listModel.getSize() - 1) {
                newSelectionIndex = selectedIndex + 1;
            }
            selectListIndex(newSelectionIndex);
        }
    }

    private void cycleListSelectionUp() {
        ListModel<Snippet> listModel = list1.getModel();

        if (listModel.getSize() > 0) {
            int selectedIndex = list1.getSelectedIndex();
            int newSelectionIndex = listModel.getSize() - 1;
            if (selectedIndex > 0) {
                newSelectionIndex = selectedIndex - 1;
            }
            selectListIndex(newSelectionIndex);
        }
    }

    private void selectListIndex(int newSelectionIndex) {
        list1.setSelectedIndex(newSelectionIndex);
        list1.ensureIndexIsVisible(newSelectionIndex);
    }

    @Override
    public void display() {
        super.display();
        quickFilterSnippetsTextField.selectAll();
        quickFilterSnippetsTextField.requestFocus();
    }
}