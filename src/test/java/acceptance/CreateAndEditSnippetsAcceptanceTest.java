package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.SnippetsUpdatePollingProcess;
import com.tagmycode.plugin.gui.form.SnippetDialog;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetsCollection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreateAndEditSnippetsAcceptanceTest extends AcceptanceTestBase {

    @Test
    public void addNewSnippet() throws Exception {
        Framework framework = acceptanceFramework();
        SnippetsUpdatePollingProcess pollingProcess = framework.getPollingProcess();
        pollingProcess.start();
        clearSnippets(framework);
        framework.restoreData();

        SnippetDialog snippetDialog = new SnippetDialog(framework, null);
        Snippet snippet = resourceGenerate.aSnippet();

        snippetDialog.populateFieldsWithSnippet(snippet);
        snippetDialog.getButtonOk().doClick();

        Thread.sleep(500);

        SnippetsCollection snippets = framework.getData().getSnippets();
        assertEquals(1, snippets.size());
        Snippet actualSnippet = snippets.get(0);

        assertEquals(3, actualSnippet.getLocalId());
        assertEquals(0, actualSnippet.getId());
    }

    @Test
    public void editASnippet() throws Exception {
        Framework framework = acceptanceFramework();
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

        Thread.sleep(500);

        assertEquals(1, framework.getData().getSnippets().size());
        assertEquals("new title", framework.getData().getSnippets().firstElement().getTitle());
    }

    private void clearSnippets(Framework framework) throws java.sql.SQLException {
        framework.getData().getStorageEngine().getDbService().snippetDao().deleteBuilder().delete();
    }
}