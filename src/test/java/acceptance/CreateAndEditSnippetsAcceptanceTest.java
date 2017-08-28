package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.plugin.operation.DeleteSnippetOperation;
import com.tagmycode.sdk.SyncSnippets;
import com.tagmycode.sdk.exception.TagMyCodeApiException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetsCollection;
import com.tagmycode.sdk.model.SnippetsDeletions;
import org.junit.Test;
import support.FrameworkBuilder;
import support.StorageEngineBuilder;
import support.TagMyCodeMockBuilder;

import java.io.IOException;
import java.sql.SQLException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

public class CreateAndEditSnippetsAcceptanceTest extends AcceptanceTestBase {

    @Test
    public void addASnippetBeforeAndAfterSync() throws Exception {
        framework = new FrameworkBuilder(createStorageEngineWithoutNetworking()).build();

        SnippetsCollection changedSnippets = new SnippetsCollection(resourceGenerate.aSnippet().setLocalId(1).setId(88).setTitle("The title from server"));
        mockTagMyCode().setSyncSnippets(
                new SyncSnippets(changedSnippets, new SnippetsDeletions()));

        framework.start();

        SnippetDialog snippetDialog = framework.showEditSnippetDialog(
                resourceGenerate.aSnippet().setId(0).setTitle("The title")
        );
        clickOnButton(snippetDialog.getButtonOk());

        assertEquals("(*) The title", getTableModel().getValueAt(0, SnippetsTableModel.TITLE));

        forceSync();

        assertEquals("The title from server", getTableModel().getValueAt(0, SnippetsTableModel.TITLE));
    }

    @Test
    public void deleteASnippetLocally() throws Exception {
        framework = new FrameworkBuilder(
                createStorageEngineWithoutNetworking()
                        .withSnippetsCollection(new SnippetsCollection(resourceGenerate.aSnippet()))
        ).build();
        mockTagMyCode();

        framework.start();

        Snippet snippetAtRow = getSnippetAtRow(0);

        softDeleteSnippet(snippetAtRow);

        assertNull(getSnippetAtRow(0));
        assertEquals(1, framework.getStorageEngine().getSnippetsStorage().findDeleted().size());

        forceSync();

        assertEquals(0, framework.getStorageEngine().getSnippetsStorage().findDeleted().size());
    }

    @Test
    public void deleteSnippetFromServer() throws Exception {
        framework = new FrameworkBuilder(
                createStorageEngineWithoutNetworking()
                        .withSnippetsCollection(new SnippetsCollection(resourceGenerate.aSnippet().setId(100)))
        ).build();
        SnippetsDeletions deletedSnippets = new SnippetsDeletions(100);
        mockTagMyCode().setSyncSnippets(new SyncSnippets(new SnippetsCollection(), deletedSnippets));

        framework.start();

        assertNotNull(getSnippetAtRow(0));

        forceSync();

        assertNull(getSnippetAtRow(0));
    }

    @Test
    public void deleteInvalidSnippetId() throws Exception {
        framework = new FrameworkBuilder(
                createStorageEngineWithoutNetworking()
                        .withSnippetsCollection(new SnippetsCollection(resourceGenerate.aSnippet().setId(100)))
        ).build();

        TagMyCodeMockBuilder tagMyCodeMockBuilder = mockTagMyCode();
        doThrow(new TagMyCodeApiException()).when(tagMyCodeMockBuilder.getMock()).syncSnippets((SnippetsCollection) any(), (SnippetsDeletions) any());

        framework.start();

        forceSync();

    }

    @Test
    public void addNewSnippet() throws Exception {
        framework = new FrameworkBuilder().
                setStorageEngine(createStorageEngineWithData())
                .build();

        forceSync();

        clearSnippets(framework);
        framework.restoreData();

        SnippetDialog snippetDialog = new SnippetDialog(framework, null);

        snippetDialog.populateFieldsWithSnippet(resourceGenerate.aSnippet());
        clickOnButton(snippetDialog.getButtonOk());

        SnippetsCollection snippets = framework.getData().getSnippets();
        assertEquals(1, snippets.size());
        Snippet actualSnippet = snippets.firstElement();

        assertEquals(3, actualSnippet.getLocalId());
        assertEquals(0, actualSnippet.getId());
    }

    @Test
    public void editASnippet() throws Exception {
        Framework framework = new FrameworkBuilder().
                setStorageEngine(createStorageEngineWithData())
                .build();
        clearSnippets(framework);

        SnippetsCollection snippetCollection = new SnippetsCollection();
        Snippet originalSnippet = resourceGenerate.aSnippet().setId(0).setTitle("original title");
        snippetCollection.add(originalSnippet);
        framework.getData().getStorageEngine().saveSnippets(snippetCollection);
        framework.restoreData();
        framework.getData().saveAll();

        SnippetDialog snippetDialog = new SnippetDialog(framework, null);

        snippetDialog.setEditableSnippet(originalSnippet);
        snippetDialog.getTitleBox().setText("new title");
        clickOnButton(snippetDialog.getButtonOk());

        assertEquals(1, framework.getData().getSnippets().size());
        assertEquals("new title", framework.getData().getSnippets().firstElement().getTitle());
    }

    private void clearSnippets(Framework framework) throws java.sql.SQLException {
        framework.getData().getStorageEngine().getDbService().snippetDao().deleteBuilder().delete();
    }

    private TagMyCodeMockBuilder mockTagMyCode() throws Exception {
        return new TagMyCodeMockBuilder(framework);
    }

    private StorageEngineBuilder createStorageEngineWithoutNetworking() throws TagMyCodeStorageException, SQLException, TagMyCodeJsonException, IOException {
        return new StorageEngineBuilder().withNetworkEnabledFlag(false);
    }

    private void softDeleteSnippet(Snippet snippet) throws InterruptedException {
        Thread start = new DeleteSnippetOperation(framework.getMainWindow().getSnippetsTab(), snippet).start();
        start.join();
    }

    private void forceSync() throws InterruptedException {
        enableNetworking();
        Thread.sleep(500);
        framework.getPollingProcess().forceScheduleUpdate();
        Thread.sleep(1000);
    }

    private void enableNetworking() {
        framework.getMainWindow().getSnippetsTab().setNetworkingEnabled(true);
    }
}