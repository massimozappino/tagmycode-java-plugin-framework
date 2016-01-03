package com.tagmycode.plugin.gui;

import javax.swing.*;

public abstract class AbstractSnippetsListGui extends AbstractGui implements ISnippetsListGui, IAbstractGUI {
    public abstract JComponent getSnippetsComponent();
}
