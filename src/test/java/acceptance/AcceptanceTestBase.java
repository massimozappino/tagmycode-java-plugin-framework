package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.TableModelSnippetNotFoundException;
import com.tagmycode.plugin.gui.form.SnippetsTab;
import com.tagmycode.plugin.gui.table.SnippetsTableModel;
import com.tagmycode.sdk.model.Snippet;
import support.AbstractTestBase;

import javax.swing.*;
import java.awt.*;

public class AcceptanceTestBase extends AbstractTestBase {
    protected Framework framework;

    protected void showComponentInFrame(Component component, int milliseconds) throws Exception {
        JFrame jFrame = new JFrame();
        jFrame.add(component);
        jFrame.pack();
        jFrame.setMinimumSize(new Dimension(900, 200));
        jFrame.setVisible(true);
        Thread.sleep(milliseconds);
    }

    protected void showComponentInFrame(Component component) throws Exception {
        showComponentInFrame(component, 3000);
    }

    protected void clickOnButton(JButton button) throws InterruptedException {
        button.doClick();
        Thread.sleep(400);
    }

    protected SnippetsTableModel getTableModel() {
        return framework.getMainWindow().getSnippetsTab().getSnippetsModel();
    }
    protected Snippet getSnippetAtRow(int row) throws TableModelSnippetNotFoundException {
        Snippet snippet = null;
        try {
            snippet = getTableModel().getSnippetAt(row);
        } catch (TableModelSnippetNotFoundException ignore) {
        }
        return snippet;
    }

    protected void selectTableRow(int index) {
        SnippetsTab snippetsTab = framework.getMainWindow().getSnippetsTab();
        JTable snippetsComponent = snippetsTab.getSnippetsTable().getSnippetsComponent();
        snippetsComponent.setRowSelectionInterval(index, index);
    }
}
