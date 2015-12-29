package com.tagmycode.plugin.gui.form;

import com.tagmycode.plugin.AbstractTest;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.gui.NoResultsFoundSnippetItem;
import com.tagmycode.plugin.gui.SnippetsJList;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.model.ModelCollection;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Before;
import org.junit.Test;
import support.FakeDocumentInsertText;

import javax.swing.*;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class SearchSnippetDialogTest extends AbstractTest {

    private Framework framework;

    @Before
    public void initFramework() {
        framework = createFramework();
    }

    @Test
    public void noResultsFound() throws Exception {
        mockClientSearchJavaGetsResults(framework);
        SearchSnippetDialog searchSnippetDialog = createSearchSnippetDialog();
        SnippetsJList snippetsList = searchSnippetDialog.getSnippetsList();
        makeASearchWithoutResultsAndWait(searchSnippetDialog);

        assertSelectedFirstElementIs(snippetsList, null);
        assertResultSizeIs(snippetsList, 1);
        assertTrue(snippetsList.getModel().getElementAt(0) instanceof NoResultsFoundSnippetItem);
        assertResultsLabelIs(searchSnippetDialog, "0 snippets found");

        makeASearchWithResultsAndWait(searchSnippetDialog);

        assertResultSizeIs(snippetsList, 2);
        assertSelectedFirstElementIs(snippetsList, resourceGenerate.aSnippetCollection().get(0));
    }

    @Test
    public void nullDocumentInsertDoNotShowInsertButton() throws Exception {
        SearchSnippetDialog searchSnippetDialog = new SearchSnippetDialog(null, framework, null);
        assertFalse(searchSnippetDialog.getInsertButton().isVisible());
    }

    @Test
    public void buttonsAreActiveOnlyWhenSnippetIsSelected() throws Exception {
        mockClientSearchJavaGetsResults(framework);
        SearchSnippetDialog searchSnippetDialog = createSearchSnippetDialog();

        assertInsertButtonIsDisabled(searchSnippetDialog);

        makeASearchWithResultsAndWait(searchSnippetDialog);
        searchSnippetDialog.getSnippetsList().setSelectedIndex(0);
        assertInsertButtonIsEnabled(searchSnippetDialog);

        makeASearchWithResultsAndWait(searchSnippetDialog);
        assertInsertButtonIsDisabled(searchSnippetDialog);

        makeASearchWithoutResultsAndWait(searchSnippetDialog);
        searchSnippetDialog.getSnippetsList().setSelectedIndex(0);
        assertInsertButtonIsDisabled(searchSnippetDialog);
    }

    @Test
    public void whileSearchingActionLabelIsEmpty() throws Exception {
        mockClientSearchJavaGetsResults(framework);

        SearchSnippetDialog searchSnippetDialog = createSearchSnippetDialog();
        assertResultsLabelIs(searchSnippetDialog, "");

        makeASearchWithResults(searchSnippetDialog);
        assertResultsLabelIs(searchSnippetDialog, "");

        Thread.sleep(1000);

        assertResultsLabelIs(searchSnippetDialog, "2 snippets found");
    }

    private void assertResultsLabelIs(SearchSnippetDialog searchSnippetDialog, String expected) {
        assertEquals(expected, searchSnippetDialog.getResultsFoundLabel().getText());
    }

    private void makeASearchWithoutResultsAndWait(SearchSnippetDialog searchSnippetDialog) throws InterruptedException {
        makeASearchAndWait(searchSnippetDialog, "none");
    }

    private void makeASearchWithResultsAndWait(SearchSnippetDialog searchSnippetDialog) throws InterruptedException {
        makeASearchAndWait(searchSnippetDialog, "java");
    }

    private void makeASearchWithResults(SearchSnippetDialog searchSnippetDialog) throws InterruptedException {
        makeASearch(searchSnippetDialog, "java");
    }

    private void assertInsertButtonIsDisabled(SearchSnippetDialog searchSnippetDialog) {
        assertFalse(searchSnippetDialog.getInsertButton().isEnabled());
    }

    private void assertInsertButtonIsEnabled(SearchSnippetDialog searchSnippetDialog) {
        assertEquals(true, searchSnippetDialog.getInsertButton().isEnabled());
    }


    private SearchSnippetDialog createSearchSnippetDialog() {
        return new SearchSnippetDialog(new FakeDocumentInsertText(), framework, null);
    }

    private void assertResultSizeIs(JList resultList, int expected) {
        assertEquals(expected, resultList.getModel().getSize());
    }

    private void assertSelectedFirstElementIs(JList resultList, Object expected) {
        resultList.setSelectedIndex(0);
        assertEquals(expected, resultList.getSelectedValue());
    }

    private void makeASearchAndWait(SearchSnippetDialog searchSnippetDialog, String query) throws InterruptedException {
        makeASearch(searchSnippetDialog, query);
        // TODO transform to wait for condition
        Thread.sleep(200);
    }

    private void makeASearch(SearchSnippetDialog searchSnippetDialog, String query) {
        searchSnippetDialog.getSearchTextBox().setText(query);
        searchSnippetDialog.getSearchButton().doClick();
    }

    protected void mockClientSearchJavaGetsResults(Framework framework) throws Exception {
        TagMyCode mockedTagMyCode = getMockedTagMyCode(framework);

        when(mockedTagMyCode.searchSnippets("java")).thenReturn(resourceGenerate.aSnippetCollection());
        when(mockedTagMyCode.searchSnippets("none")).thenReturn(new ModelCollection<Snippet>());
    }

}