package com.tagmycode.plugin.gui;

import com.tagmycode.plugin.IconResources;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class CutCopyPastePopup extends JPopupMenu implements ActionListener {

    private JTextComponent target;

    public CutCopyPastePopup(final JTextComponent target) {
        this.target = target;

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(this);
        cutItem.setActionCommand("cut");
        cutItem.setIcon(IconResources.createImageIcon("cut.png"));

        add(cutItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(this);
        copyItem.setActionCommand("copy");
        copyItem.setIcon(IconResources.createImageIcon("copy.png"));

        add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(this);
        pasteItem.setActionCommand("paste");
        pasteItem.setIcon(IconResources.createImageIcon("paste.png"));
        add(pasteItem);

        add(new JSeparator());

        JMenuItem selectAllItem = new JMenuItem("Select all");
        selectAllItem.addActionListener(this);
        selectAllItem.setActionCommand("selectall");
        add(selectAllItem);

        target.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        if (!target.isEditable()) {
            pasteItem.setEnabled(false);
            cutItem.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("cut".equals(e.getActionCommand())) {
            target.cut();
        } else if ("copy".equals(e.getActionCommand())) {
            target.copy();
        } else if ("paste".equals(e.getActionCommand())) {
            target.paste();
        } else if ("selectall".equals(e.getActionCommand())) {
            target.selectAll();
        }
    }
}
