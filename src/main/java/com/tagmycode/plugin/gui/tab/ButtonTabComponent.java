package com.tagmycode.plugin.gui.tab;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class ButtonTabComponent extends JPanel {
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
    private final JTabbedPane pane;

    public ButtonTabComponent(final JTabbedPane pane) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }

        this.pane = pane;
        setOpaque(false);

        JLabel label = new JLabel() {
            public String getText() {
                int i = currentTabIndex(pane);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };

        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton button = new TabButton();
        add(button);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                    removeCurrentTab();
                }
                if (e.getClickCount() == 1) {
                    pane.setSelectedIndex(currentTabIndex(pane));
                }
            }
        });
    }

    private int currentTabIndex(JTabbedPane pane) {
        return pane.indexOfTabComponent(ButtonTabComponent.this);
    }

    private void removeCurrentTab() {
        int i = currentTabIndex(pane);
        if (i != -1) {
            pane.remove(i);
        }
    }

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            super("x");
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close Tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            removeCurrentTab();
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

    }
}
