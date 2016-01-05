package com.tagmycode.plugin.gui.tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

public class JClosableTabbedPane extends JTabbedPane {
    public JClosableTabbedPane() {
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JTabbedPane tabPane = (JTabbedPane) e.getSource();
                int dir = e.getWheelRotation();
                int selIndex = tabPane.getSelectedIndex();
                int maxIndex = tabPane.getTabCount() - 1;
                selIndex += dir;
                if (selIndex >= maxIndex) {
                    selIndex = maxIndex;
                }
                if (selIndex <= 0) {
                    selIndex = 0;
                }
                tabPane.setSelectedIndex(selIndex);
            }
        });
    }

    public void addClosableTab(String title, Component component) {
        add(title, component);
        int index = getTabCount() - 1;
        setTabComponentAt(index, new ButtonTabComponent(this));
        setSelectedIndex(index);
    }

    public ButtonTabComponent getClosableTab(int index) {
        Component tabComponentAt = getTabComponentAt(index);
        return tabComponentAt instanceof ButtonTabComponent ? (ButtonTabComponent) tabComponentAt : null;
    }
}
