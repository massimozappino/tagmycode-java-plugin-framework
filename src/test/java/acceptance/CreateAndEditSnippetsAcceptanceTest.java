package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.SyncSnippets;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetsCollection;
import com.tagmycode.sdk.model.SnippetsDeletions;
import org.junit.Test;
import support.FrameworkBuilder;
import support.StorageEngineBuilder;
import support.TagMyCodeMockBuilder;

import static org.junit.Assert.assertEquals;

public class CreateAndEditSnippetsAcceptanceTest extends AcceptanceTestBase {

    @Test
    public void addASnippetBeforeAndAfterSync() throws Exception {
        Framework framework = new FrameworkBuilder(
                new StorageEngineBuilder().withNetworkEnabledFlag(false))
                .build();

        SnippetsCollection changedSnippets = new SnippetsCollection(resourceGenerate.aSnippet().setLocalId(1).setId(88).setTitle("The title from server"));
        new TagMyCodeMockBuilder(framework)
                .setSyncSnippets(new SyncSnippets(changedSnippets, new SnippetsDeletions()));

        framework.start();

        SnippetDialog snippetDialog = framework.showEditSnippetDialog(
                resourceGenerate.aSnippet()
                        .setId(0)
                        .setTitle("The title")
        );
        clickOnButton(snippetDialog.getButtonOk());

        Thread.sleep(500);

        assertEquals("(*) The title", getTableModel(framework).getValueAt(0, SnippetsTableModel.TITLE));

        clickOnButton(framework.getMainWindow().getSnippetsTab().getButtonNetworking());

        framework.getPollingProcess().forceScheduleUpdate();

        Thread.sleep(500);

        assertEquals("The title from server", getTableModel(framework).getValueAt(0, SnippetsTableModel.TITLE));
    }


    // conflicts
    // add a snippet
    // sync with server
    // delete snippet from server (do not sync client)
    // edit that snippet in client
    // sync
    // i will show a new snippet with new id with [Conflict] extension on title

    @Test
    public void addNewSnippet() throws Exception {
        final Framework framework = new FrameworkBuilder().
                setStorageEngine(createStorageEngineWithData())
                .build();

        SnippetsUpdatePollingProcess pollingProcess = framework.getPollingProcess();
        pollingProcess.start();
        clearSnippets(framework);
        framework.restoreData();

        SnippetDialog snippetDialog = new SnippetDialog(framework, null);
        Snippet snippet = resourceGenerate.aSnippet();

        snippetDialog.populateFieldsWithSnippet(snippet);
        snippetDialog.getButtonOk().doClick();

        Thread.sleep(100);

        SnippetsCollection snippets = framework.getData().getSnippets();
        assertEquals(1, snippets.size());
        Snippet actualSnippet = snippets.get(0);

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
        snippetDialog.getButtonOk().doClick();

        Thread.sleep(100);

        assertEquals(1, framework.getData().getSnippets().size());
        assertEquals("new title", framework.getData().getSnippets().firstElement().getTitle());
    }

    private void clearSnippets(Framework framework) throws java.sql.SQLException {
        framework.getData().getStorageEngine().getDbService().snippetDao().deleteBuilder().delete();
    }
}