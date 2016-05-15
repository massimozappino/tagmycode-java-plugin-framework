package com.tagmycode.plugin.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MoveUpDownFilterFieldKeyListener implements KeyListener {
    private JTable jTable;

    public MoveUpDownFilterFieldKeyListener(JTable jTable) {
        this.jTable = jTable;
    }

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

    private void cycleTableSelectionRows(String direction) {
        int size = jTable.getRowCount();
        if (size > 0) {
            int selectedIndex = jTable.getSelectedRow();
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
        jTable.setRowSelectionInterval(newSelectionIndex, newSelectionIndex);
        jTable.scrollRectToVisible(new Rectangle(jTable.getCellRect(newSelectionIndex, 0, true)));
    }
}
