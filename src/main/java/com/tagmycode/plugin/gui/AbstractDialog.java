package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.sdk.exception.TagMyCodeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class AbstractDialog extends AbstractGui implements IonErrorCallback {
    protected final Framework framework;
    private final Frame parentFrame;
    private final JDialog dialog;

    public AbstractDialog(Framework framework, Frame parent) {
        dialog = new JDialog(parent, true);
        parentFrame = parent;
        this.framework = framework;
    }

    protected abstract void initWindow();

    protected void defaultInitWindow() {
        dialog.setContentPane(getMainPanel());
        dialog.getRootPane().setDefaultButton(getButtonOk());

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
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        getMainPanel().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        initPopupMenuForJTextComponents(dialog);
    }

    protected abstract void onOK();

    public abstract JButton getButtonOk();

    protected abstract JButton getButtonCancel();

    private void onCancel() {
        closeDialog();
    }

    public void closeDialog() {
        dialog.dispose();
    }

    public void showAtCenter() {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                CenterLocation location = new CenterLocation(parentFrame, dialog);
                dialog.setLocation(location.getX(), location.getY());
                dialog.setVisible(true);
            }
        });

    }

    @Override
    public void onError(TagMyCodeException exception) {
        framework.manageTagMyCodeExceptions(exception);
    }

    public Framework getFramework() {
        return framework;
    }

    public JDialog getDialog() {
        return dialog;
    }
}
