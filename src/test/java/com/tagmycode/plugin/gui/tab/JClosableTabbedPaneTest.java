package com.tagmycode.plugin.gui.tab;

import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertEquals;

public class JClosableTabbedPaneTest {

    private JClosableTabbedPane tabbedPane = new JClosableTabbedPane();

    @Test
    public void addClosableTab() {
        tabbedPane.addTab("Not closable", new JPanel());
        assertEquals(0, tabbedPane.getSelectedIndex());

        tabbedPane.addClosableTab("Closable 1", new JPanel());
        assertEquals(1, tabbedPane.getSelectedIndex());
    }

    @Test
    public void closeTab() {
        tabbedPane = new JClosableTabbedPane();
        tabbedPane.addTab("Not closable", new JPanel());
        tabbedPane.addClosableTab("Closable 1", new JPanel());
        tabbedPane.addClosableTab("Closable 2", new JPanel());
        tabbedPane.addClosableTab("Closable 3", new JPanel());
        tabbedPane.addClosableTab("Closable 4", new JPanel());

        tabbedPane.setSelectedIndex(2);
        tabbedPane.getClosableTab(2).closeCurrentTab();
        assertEquals(4, tabbedPane.getTabCount());
        assertEquals(2, tabbedPane.getSelectedIndex());

        tabbedPane.setSelectedIndex(3);
        tabbedPane.getClosableTab(3).closeCurrentTab();
        assertEquals(3, tabbedPane.getTabCount());
        assertEquals(2, tabbedPane.getSelectedIndex());
    }
}