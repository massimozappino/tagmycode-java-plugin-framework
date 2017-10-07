package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.sdk.exception.TagMyCodeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class AbstractDialog extends AbstractGui implements IOnErrorCallback {
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
        getMainComponent().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.setContentPane(getMainComponent());
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
        getMainComponent().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        initPopupMenuForJTextComponents(dialog);
    }

    protected abstract void onOK();

    public abstract JButton getButtonOk();

    protected abstract JButton getButtonCancel();

    protected void onCancel() {
        closeDialog();
    }

    public void closeDialog() {
        dialog.dispose();
    }

    public void hideDialog() {
        dialog.setVisible(false);
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

    public void display() {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                CenterLocation location = new CenterLocation(parentFrame, dialog);
                dialog.setLocation(location.getX(), location.getY());
                dialog.setVisible(true);
            }
        });
    }

    protected void hideOnFocusLost() {
        getDialog().setModal(false);
        getDialog().addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) {
                hideDialog();
            }

            public void windowGainedFocus(WindowEvent e) {
            }
        });
    }
}
