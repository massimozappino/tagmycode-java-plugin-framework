package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.GuiThread;
import com.tagmycode.plugin.gui.*;
import com.tagmycode.sdk.exception.TagMyCodeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class Windowable extends AbstractGui implements IOnErrorCallback {

    private Window window;
    private WindowType.Type windowType;
    private Frame parentFrame;
    protected Framework framework;
    private String title;

    public Windowable(Framework framework, Frame parent, WindowType.Type windowType) {
        this.framework = framework;
        this.windowType = windowType;
        parentFrame = parent;
        if (isJDialog()) {
            window = new JDialog(parentFrame, true);
        } else {
            JFrame jFrame = new JFrame();
            jFrame.setIconImage(GuiUtil.loadImage("images/tagmycode_app.png").getImage());
            window = jFrame;
        }
    }

    public Windowable(Framework framework, Frame parent) {
        this(framework, parent, WindowType.getDefaultType());
    }

    protected abstract void initWindow();

    protected void defaultInitWindow() {
        getMainComponent().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ((RootPaneContainer) window).setContentPane(getMainComponent());
        ((RootPaneContainer) window).getRootPane().setDefaultButton(getButtonOk());
        if (isJDialog()) {
            getJDialog().setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        } else {
            getJFrame().setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        }

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

        window.addWindowListener(new WindowAdapter() {
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

        initPopupMenuForJTextComponents(window);
    }

    public void display() {
        new GuiThread().execute(new Runnable() {

            @Override
            public void run() {
                CenterLocation location = new CenterLocation(parentFrame, window, CenterLocationType.CENTER_FRAME);
                window.setLocation(location.getX(), location.getY());
                window.setVisible(true);
                window.revalidate();
                window.repaint();
            }
        });
    }

    public Component getWindow() {
        return window;
    }

    public void setSize(int i, int i1) {
        window.setSize(i, i1);
    }

    public void setResizable(boolean flag) {
        if (isJDialog()) {
            getJDialog().setResizable(flag);
        } else {
            getJFrame().setResizable(flag);
        }
    }

    public void setUndecorated(boolean flag) {
        if (isJDialog()) {
            getJDialog().setUndecorated(flag);
        }
    }


    public void setModal(boolean flag) {
        if (isJDialog()) {
            getJDialog().setModal(flag);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (isJDialog()) {
            getJDialog().setTitle(title);
        } else {
            getJFrame().setTitle(title);
        }
    }

    public void setMinimumSize(Dimension dimension) {
        if (isJDialog()) {
            getJDialog().setMinimumSize(dimension);
        } else {
            getJFrame().setMinimumSize(dimension);
        }
    }

    public JRootPane getRootPane() {
        return isJDialog() ? getJDialog().getRootPane() : getJFrame().getRootPane();
    }

    @Override
    public void onError(TagMyCodeException exception) {
        framework.manageTagMyCodeExceptions(exception);
    }


    protected abstract void onOK();

    public abstract JButton getButtonOk();

    protected abstract JButton getButtonCancel();

    protected void onCancel() {
        closeDialog();
    }

    public void closeDialog() {
        window.dispose();
    }

    public void hideDialog() {
        window.setVisible(false);
    }

    private boolean isJDialog() {
        return windowType == WindowType.Type.JDIALOG;
    }

    private JDialog getJDialog() {
        return (JDialog) window;
    }

    private JFrame getJFrame() {
        return (JFrame) window;
    }

    public Framework getFramework() {
        return framework;
    }

    public String getTitle() {
        return title;
    }

    protected void hideOnFocusLost() {
        setModal(false);
        window.addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) {
                hideDialog();
            }

            public void windowGainedFocus(WindowEvent e) {
            }
        });
    }

    public void dispose() {
        window.dispose();
    }
}
