package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.sdk.exception.TagMyCodeException;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public abstract class AbstractDialog extends JDialog implements IonErrorCallback {
    protected final Framework framework;
    private final Frame parentFrame;

    public AbstractDialog(Framework framework, Frame parent) {
        super(parent, true);
        parentFrame = parent;
        this.framework = framework;
    }

    protected abstract void initWindow();

    protected void defaultInitWindow() {
        setContentPane(getContentPanePanel());
        getRootPane().setDefaultButton(getButtonOk());

        getButtonOk().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        getButtonCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        getContentPanePanel().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        initPopupMenuForJTextComponents();
    }

    private void initPopupMenuForJTextComponents() {
        ArrayList<Component> components = getAllComponents(this);

        for (Component component : components) {
            if (component instanceof JTextComponent) {
                new CutCopyPastePopup((JTextComponent) component);
            }
        }
    }

    private ArrayList<Component> getAllComponents(final Container container) {
        Component[] components = container.getComponents();
        ArrayList<Component> compList = new ArrayList<Component>();
        for (Component component : components) {
            compList.add(component);
            if (component instanceof Container) {
                compList.addAll(getAllComponents((Container) component));
            }
        }
        return compList;
    }

    protected abstract void onOK();

    public abstract JButton getButtonOk();

    protected abstract JButton getButtonCancel();

    protected abstract JPanel getContentPanePanel();

    private void onCancel() {
        closeDialog();
    }

    public void closeDialog() {
        dispose();
    }

    public void showAtCenter() {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                CenterLocation location = new CenterLocation(parentFrame, AbstractDialog.this);
                setLocation(location.getX(), location.getY());
                setVisible(true);
            }
        });

    }

    public void onError(TagMyCodeException exception) {
        framework.manageTagMyCodeExceptions(exception);
    }

    public Framework getFramework() {
        return framework;
    }
}
