package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.FilterLanguagesLoader;
import com.tagmycode.plugin.filter.FilterLanguages;
import com.tagmycode.plugin.filter.LanguageFilterEntry;
import com.tagmycode.plugin.gui.AbstractGui;
import com.tagmycode.plugin.gui.GuiUtil;
import com.tagmycode.plugin.operation.FilterSnippetsOperation;
import com.tagmycode.sdk.model.Language;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Enumeration;

public class FiltersPanelForm extends AbstractGui {
    private JPanel mainComponent;
    private FilterSnippetsOperation filterSnippetsOperation;

    private JTree tree;
    private FilterLanguagesLoader filterLanguagesLoader;
    private DefaultTreeModel treeModel;

    public FiltersPanelForm(FilterSnippetsOperation filterSnippetsOperation, Data data) {
        this.filterSnippetsOperation = filterSnippetsOperation;
        filterLanguagesLoader = new FilterLanguagesLoader(data);
        configureTree();
        JScrollPane languagesScrollPane = new JScrollPane(tree);
        GuiUtil.removeBorder(languagesScrollPane);
        JPanel languagesPanel = new JPanel();
        languagesPanel.setLayout(new BorderLayout());
        languagesPanel.add(languagesScrollPane, BorderLayout.CENTER);
        mainComponent.add(languagesPanel, BorderLayout.CENTER);
    }

    private void configureTree() {
        tree = new JTree();
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeModel = (DefaultTreeModel) tree.getModel();
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();

                Language language = null;
                if (node != null) {
                    language = languageFromNode(node);
                }
                filterLanguage(language);
            }
        });
    }

    private Language languageFromNode(DefaultMutableTreeNode node) {
        if (node != null && node.isLeaf() && node.getUserObject() instanceof LanguageFilterEntry) {
            return ((LanguageFilterEntry) node.getUserObject()).getLanguage();
        }
        return null;
    }

    @Override
    public JComponent getMainComponent() {
        return mainComponent;
    }

    private void filterLanguage(Language language) {
        filterSnippetsOperation.getSnippetsTable().getJTable().clearSelection();
        filterSnippetsOperation.setFilterLanguage(language);
        filterSnippetsOperation.filter();
    }

    public void refresh() {
        FilterLanguages load = filterLanguagesLoader.load();
        Language filterLanguage = filterSnippetsOperation.getFilterLanguage();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.setUserObject("Languages");
        root.removeAllChildren();

        for (LanguageFilterEntry languageFilterEntry : load) {
            root.add(new DefaultMutableTreeNode(languageFilterEntry));
        }

        treeModel.reload(root);
        selectLanguageOnTree(filterLanguage);
    }

    private void selectLanguageOnTree(Language language) {
        TreePath treePath = findPathForLanguages((DefaultMutableTreeNode) treeModel.getRoot(), language);
        tree.setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);
    }

    private TreePath findPathForLanguages(DefaultMutableTreeNode root, Language language) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            Language currentLanguage = languageFromNode(node);
            if (currentLanguage != null && currentLanguage.equals(language)) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    public void reset() {
        selectLanguageOnTree(null);
        filterLanguage(null);
    }
}
