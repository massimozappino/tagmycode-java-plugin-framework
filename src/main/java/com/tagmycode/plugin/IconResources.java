package com.tagmycode.plugin;


import javax.swing.*;

public class IconResources {
    public static ImageIcon createImageIcon(String iconName) {
        return new ImageIcon(IconResources.class.getClassLoader().getResource("icons/" + iconName));
    }
}
