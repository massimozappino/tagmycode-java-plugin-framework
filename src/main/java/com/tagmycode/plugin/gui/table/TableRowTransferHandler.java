package com.tagmycode.plugin.gui.table;

import com.tagmycode.plugin.gui.form.SnippetsPanel;

import javax.activation.ActivationDataFlavor;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TableRowTransferHandler extends TransferHandler {
    private final JTable jTable;
    private SnippetsPanel snippetsPanel = null;
    private final DataFlavor localObjectFlavor = new ActivationDataFlavor(String.class, "String object");

    public TableRowTransferHandler(SnippetsPanel snippetsPanel) {
        this.snippetsPanel = snippetsPanel;
        this.jTable = snippetsPanel.getSnippetsTable().getJTable();
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == jTable);
        return new StringSelection(snippetsPanel.getSnippetsTable().getSelectedSnippet().getCode());
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    @Override
    public boolean canImport(TransferSupport info) {
        return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
    }

    @Override
    public boolean importData(TransferSupport info) {
        try {
            String data = (String) info.getTransferable().getTransferData(localObjectFlavor);
            snippetsPanel.newSnippetAction(data);
            return true;
        } catch (UnsupportedFlavorException | IOException ignored) {
            return false;
        }
    }
}
