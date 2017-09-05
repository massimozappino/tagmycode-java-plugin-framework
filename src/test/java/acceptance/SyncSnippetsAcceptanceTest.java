package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.plugin.operation.DeleteSnippetOperation;
import com.tagmycode.sdk.SyncSnippets;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.exception.TagMyCodeApiException;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetsCollection;
import com.tagmycode.sdk.model.SnippetsDeletions;
import com.tagmycode.sdk.model.SnippetsStorage;
import org.junit.Test;
import support.FrameworkBuilder;
import support.StorageEngineBuilder;
import support.TagMyCodeMockBuilder;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;


public class SyncSnippetsAcceptanceTest extends AcceptanceTestBase {

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
                        .withSnippetsCollection(new SnippetsCollection(resourceGenerate.aSnippet().setId(1)))
        ).build();
        mockTagMyCode();

        framework.start();

        softDeleteSnippet(getSnippetAtRow(0));

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
        mockTagMyCode().setSyncSnippets(new SyncSnippets(new SnippetsCollection(), new SnippetsDeletions(100)));

        framework.start();

        assertEquals(100, getSnippetAtRow(0).getId());

        forceSync();

        assertNull(getSnippetAtRow(0));
    }

    @Test
    public void deleteInvalidSnippetId() throws Exception {
        framework = new FrameworkBuilder(
                createStorageEngineWithoutNetworking()
                        .withSnippetsCollection(
                                new SnippetsCollection(resourceGenerate.aSnippet().setTitle("sync snippet").setId(99)))
        ).build();

        TagMyCode tagMyCode = mockTagMyCode().getMock();
        doThrow(new TagMyCodeApiException()).when(tagMyCode).syncSnippets(new SnippetsCollection(), new SnippetsDeletions(100));

        framework.start();
        assertEquals(0, framework.getStorageEngine().getSnippetsStorage().findDeleted().size());
        assertEquals(1, framework.getData().getSnippets().size());

        forceSync();

        assertEquals(0, framework.getStorageEngine().getSnippetsStorage().findDeleted().size());
        assertEquals(1, framework.getData().getSnippets().size());
    }

    @Test
    public void localDeletionWorksAlsoForServer() throws Exception {
        framework = new FrameworkBuilder(
                createStorageEngineWithoutNetworking()
                        .withSnippetsCollection(
                                new SnippetsCollection(resourceGenerate.aSnippet().setTitle("sync snippet").setId(99)))
        ).build();

        TagMyCode mock = mockTagMyCode().getMock();

        framework.start();
        softDeleteSnippet(getSnippetAtRow(0));

        forceSync();

        verify(mock, times(1)).syncSnippets(new SnippetsCollection(), new SnippetsDeletions(99));
    }

    @Test
    public void addNewSnippet() throws Exception {
        framework = new FrameworkBuilder(createStorageEngineWithoutNetworking()).build();
        mockTagMyCode().setSyncSnippets(new SyncSnippets(
                new SnippetsCollection(resourceGenerate.aSnippet().setLocalId(1).setId(1))
                , new SnippetsDeletions()));

        framework.start();

        SnippetDialog snippetDialog = new SnippetDialog(framework, null);
        snippetDialog.populateFieldsWithSnippet(resourceGenerate.aSnippet());
        clickOnButton(snippetDialog.getButtonOk());

        SnippetsCollection snippets = framework.getData().getSnippets();
        assertEquals(1, snippets.size());
        Snippet actualSnippet = snippets.firstElement();

        assertEquals(1, actualSnippet.getLocalId());
        assertEquals(0, actualSnippet.getId());

        forceSync();

        assertEquals(1, framework.getData().getSnippets().size());
        assertEquals(1, actualSnippet.getLocalId());
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

    @Test
    public void reSyncAfterApiError() throws Exception {
        SnippetsCollection actualSnippets = new SnippetsCollection(
                resourceGenerate.aSnippet().setId(1).setLocalId(1).setTitle("visible synced"),
                resourceGenerate.aSnippet().setId(0).setLocalId(2).setTitle("visible dirty not synced").setDirty(true),
                resourceGenerate.aSnippet().setId(2).setLocalId(3).setTitle("deleted").setDeleted(true)
        );
        framework = new FrameworkBuilder(createStorageEngineWithoutNetworking()
                .withSnippetsCollection(actualSnippets))
                .build();

        SyncSnippets syncSnippets = new SyncSnippets(
                new SnippetsCollection(
                        resourceGenerate.aSnippet().setId(99).setTitle("new from server"),
                        resourceGenerate.aSnippet().setId(100).setLocalId(2).setTitle("updated dirty from server")),
                new SnippetsDeletions());
        TagMyCodeMockBuilder tagMyCodeMockBuilder = mockTagMyCode().setSyncSnippets(syncSnippets);
        TagMyCode tagMyCode = tagMyCodeMockBuilder.getMock();
        SnippetsStorage snippetsStorage = framework.getStorageEngine().getSnippetsStorage();
        doThrow(new TagMyCodeApiException()).when(tagMyCode).syncSnippets(snippetsStorage.findDirtyNotDeleted(), snippetsStorage.findDeletedIds());

        framework.start();

        SnippetsCollection visible = framework.getStorageEngine().getSnippetsStorage().findVisible();
        assertEquals(2, visible.size());
        assertEquals(1, framework.getStorageEngine().getSnippetsStorage().findDirtyNotDeleted().size());
        assertEquals(1, framework.getStorageEngine().getSnippetsStorage().findDeleted().size());

        forceSync();

        assertEquals(0, framework.getStorageEngine().getSnippetsStorage().findDirtyNotDeleted().size());
        assertEquals(3, framework.getStorageEngine().getSnippetsStorage().findVisible().size());
        assertEquals(0, framework.getStorageEngine().getSnippetsStorage().findDeleted().size());
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

        while (true) {
            if (framework.getPollingProcess().isRunningTask()) {
                Thread.sleep(100);
            } else {
                break;
            }
        }
        Thread.sleep(1500);
    }

    private void enableNetworking() {
        framework.getMainWindow().getSnippetsTab().setNetworkingEnabled(true);
    }

    private void showFrameworkGui() throws Exception {
        showComponentInFrame(framework.getMainWindow().getMainComponent());
    }
}