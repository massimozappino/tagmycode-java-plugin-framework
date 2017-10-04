package com.tagmycode.plugin.gui.table;

import com.google.common.io.Files;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.form.SnippetsPanel;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableRowTransferHandler extends TransferHandler {
    private final JTable jTable;
    private final Framework framework;
    private SnippetsPanel snippetsPanel = null;
    private final DataFlavor stringDataFlavor = DataFlavor.stringFlavor;
    private final DataFlavor snippetDataFlavor = new DataFlavor(Snippet.class, "Snippet object");
    private File tempDir = null;

    public TableRowTransferHandler(SnippetsPanel snippetsPanel) {
        this.framework = snippetsPanel.getFramework();
        this.snippetsPanel = snippetsPanel;
        this.jTable = snippetsPanel.getSnippetsTable().getJTable();
        tempDir = Files.createTempDir();
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == jTable);
        final Snippet selectedSnippet = snippetsPanel.getSnippetsTable().getSelectedSnippet();
        final String code = selectedSnippet.getCode();

        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{
                        snippetDataFlavor,
                        stringDataFlavor,
                        DataFlavor.javaFileListFlavor
                };
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return true;
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (flavor.equals(snippetDataFlavor)) {
                    return selectedSnippet;
                } else if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                    List<File> list = new ArrayList<>();
                    if (tempDir != null) {
                        File temp = new File(tempDir, selectedSnippet.getTitle());
                        FileOutputStream out = new FileOutputStream(temp);
                        out.write(code.getBytes());
                        out.close();
                        list.add(temp);
                    }
                    return list;
                } else {
                    return code;
                }
            }
        };
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    @Override
    public boolean canImport(TransferSupport info) {
        return info.isDrop() && !info.isDataFlavorSupported(snippetDataFlavor) &&
                (info.isDataFlavorSupported(stringDataFlavor) ||
                        info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
                );
    }

    @Override
    public boolean importData(TransferSupport info) {
        String code;

        if (info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List data = (List) info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (data.size() != 1) {
                    return false;
                }

                File file = (File) data.get(0);
                showDialog(framework.getData().createSnippetFromFile(file));
                return true;

            } catch (UnsupportedFlavorException | IOException ignored) {
            }
        }

        if (info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                code = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
                showDialog(framework.getData().createSnippet("", code, null));
                return true;
            } catch (UnsupportedFlavorException | IOException ignored) {
            }
        }

        return false;
    }

    private void showDialog(Snippet snippet) {
        snippetsPanel.newSnippetAction(snippet);
    }
}
