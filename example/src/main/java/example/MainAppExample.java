package example;

import com.tagmycode.plugin.AbstractVersion;
import com.tagmycode.plugin.Browser;
import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.GuiUtil;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.SaveFilePath;
import com.tagmycode.sdk.authentication.TagMyCodeApiProduction;
import com.tagmycode.sdk.exception.TagMyCodeException;
import example.support.MessageManager;
import example.support.PasswordKeyChain;
import example.support.TaskFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;

public class MainAppExample {
    public static void main(String[] args) throws IOException, TagMyCodeException, SQLException {
        JFrame frame = new JFrame("TagMyCode Plugin Example");
        frame.setIconImage(GuiUtil.loadImage("images/tagmycode_app.png").getImage());

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setBounds(200, 200, 600, 200);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel lookAndFeelPanel = getLookAndFeelPanel(frame);
        JPanel comp = new JPanel(new BorderLayout());
        contentPane.add(comp, BorderLayout.NORTH);

        comp.add(lookAndFeelPanel, BorderLayout.WEST);
        JButton searchButton = new JButton("Search");
        comp.add(searchButton, BorderLayout.CENTER);

        SaveFilePath saveFilePath = new SaveFilePath("tagmycode_framework_example");
        FrameworkConfig frameworkConfig = new FrameworkConfig(
                saveFilePath,
                new PasswordKeyChain(saveFilePath),
                new DbService(saveFilePath.getPath()),
                new MessageManager(),
                new TaskFactory(),
                new Browser(),
                new AbstractVersion() {
                    @Override
                    public String getPluginVersion() {
                        return "1.0.0";
                    }

                    @Override
                    public String getPluginTitle() {
                        return "TagMyCode Plugin Example";
                    }
                },
                frame);
        final Framework framework = new Framework(new TagMyCodeApiProduction(), frameworkConfig, new Secret());

        framework.start();
        contentPane.add(framework.getMainFrame(), BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (framework.canOperate()) {
                    framework.showSearchDialog(new IDocumentInsertText() {
                        @Override
                        public void insertText(String text) {
                            System.out.println(text);
                        }
                    });
                }
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                try {
                    framework.closeFramework();
                } catch (TagMyCodeStorageException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    private static JPanel getLookAndFeelPanel(final Frame frame) {
        JPanel lookAndFeelPanel = new JPanel();
        final JComboBox<String> jComboBox = new JComboBox<>();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            jComboBox.addItem(info.getClassName());
        }
        lookAndFeelPanel.add(jComboBox);

        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) jComboBox.getSelectedItem();
                try {
                    UIManager.setLookAndFeel(selectedItem);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                SwingUtilities.updateComponentTreeUI(frame);

            }
        });
        return lookAndFeelPanel;
    }
}
