package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Data;
import com.tagmycode.sdk.model.Snippet;

import javax.swing.*;
import javax.swing.event.ListDataListener;
//TODO
public class SnippetsListModel extends AbstractListModel<Snippet> {
    private Data data;

    public SnippetsListModel(Data data) {
        this.data = data;
    }

    @Override
    public int getSize() {
//        return data.getSnippets().size();
        return 0;
    }

    @Override
    public Snippet getElementAt(int index) {
//        return data.getSnippets().elementAt(index);
    return null;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {

    }
}
