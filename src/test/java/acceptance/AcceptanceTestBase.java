package acceptance;

import com.tagmycode.plugin.Framework;
import support.AbstractTestBase;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class AcceptanceTestBase extends AbstractTestBase {

    protected void showComponentInFrame(Component component, int milliseconds) throws Exception {
        JFrame jFrame = new JFrame();
        jFrame.add(component);
        jFrame.pack();
        jFrame.setMinimumSize(new Dimension(200, 200));
        jFrame.setVisible(true);
        Thread.sleep(milliseconds);
    }

    protected void showComponentInFrame(Component component) throws Exception {
        showComponentInFrame(component, 2000);
    }

    protected void clickOnButton(JButton button) {
        button.doClick();
    }

    protected TableModel getTableModel(Framework framework) {
        return framework.getMainWindow().getSnippetsTab().getSnippetsTable().getSnippetsComponent().getModel();
    }
}
