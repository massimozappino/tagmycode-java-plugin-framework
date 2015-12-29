package com.tagmycode.plugin.gui;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;

public abstract class AbstractGui implements IAbstractGUI {

    public void initPopupMenuForJTextComponents(Container container) {
        ArrayList<Component> components = getAllComponents(container);

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

}
