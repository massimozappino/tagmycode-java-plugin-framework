package example;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class TextEditorDemo extends JFrame {

    public TextEditorDemo() {

        JPanel cp = new JPanel(new BorderLayout());

        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <groupId>com.tagmycode</groupId>\n" +
                "    <artifactId>tagmycode-plugin-framework</artifactId>\n" +
                "    <version>0.3-SNAPSHOT</version>\n" +
                "    <name>TagMyCode IDE Plugin Framework for Java</name>\n" +
                "    <description>A framework to develop Java IDE plugins for TagMyCode</description>\n" +
                "    <url>http://tagmycode.com</url>\n" +
                "\n" +
                "    <properties>\n" +
                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "        <tagmycode-sdk.version>0.1.3-SNAPSHOT</tagmycode-sdk.version>\n" +
                "    </properties>\n" +
                "\n" +
                "    <licenses>\n" +
                "        <license>\n" +
                "            <name>Apache License 2.0</name>\n" +
                "            <url>http://www.apache.org/licenses/LICENSE-2.0</url>\n" +
                "            <distribution>repo</distribution>\n" +
                "        </license>\n" +
                "    </licenses>\n" +
                "\n" +
                "    <parent>\n" +
                "        <groupId>org.sonatype.oss</groupId>\n" +
                "        <artifactId>oss-parent</artifactId>\n" +
                "        <version>7</version>\n" +
                "    </parent>\n" +
                "\n" +
                "    <scm>\n" +
                "        <connection>scm:git:git://github.com/massimozappino/tagmycode-java-plugin-framework.git</connection>\n" +
                "        <developerConnection>scm:git:git@github.com:massimozappino/tagmycode-java-plugin-framework.git\n" +
                "        </developerConnection>\n" +
                "        <url>https://github.com/massimozappino/tagmycode-java-plugin-framework</url>\n" +
                "        <tag>HEAD</tag>\n" +
                "    </scm>\n" +
                "\n" +
                "    <developers>\n" +
                "        <developer>\n" +
                "            <id>massimozappino</id>\n" +
                "            <name>Massimo Zappino</name>\n" +
                "            <email>massimo.zappino@gmail.com</email>\n" +
                "            <timezone>+2</timezone>\n" +
                "        </developer>\n" +
                "    </developers>\n" +
                "\n" +
                "    <build>\n" +
                "        <defaultGoal>package</defaultGoal>\n" +
                "\n" +
                "        <plugins>\n" +
                "            <plugin>\n" +
                "                <groupId>org.apache.maven.plugins</groupId>\n" +
                "                <artifactId>maven-compiler-plugin</artifactId>\n" +
                "                <version>3.1</version>\n" +
                "                <configuration>\n" +
                "                    <source>1.7</source>\n" +
                "                    <target>1.7</target>\n" +
                "                </configuration>\n" +
                "            </plugin>\n" +
                "            <plugin>\n" +
                "                <groupId>org.codehaus.mojo</groupId>\n" +
                "                <artifactId>ideauidesigner-maven-plugin</artifactId>\n" +
                "                <version>1.0-beta-1</version>\n" +
                "                <executions>\n" +
                "                    <execution>\n" +
                "                        <goals>\n" +
                "                            <goal>javac2</goal>\n" +
                "                        </goals>\n" +
                "                    </execution>\n" +
                "                </executions>\n" +
                "\n" +
                "                <configuration>\n" +
                "                    <fork>true</fork>\n" +
                "                    <debug>true</debug>\n" +
                "                    <failOnError>true</failOnError>\n" +
                "                </configuration>\n" +
                "            </plugin>\n" +
                "        </plugins>\n" +
                "    </build>\n" +
                "\n" +
                "    <distributionManagement>\n" +
                "        <snapshotRepository>\n" +
                "            <id>ossrh</id>\n" +
                "            <url>https://oss.sonatype.org/content/repositories/snapshots</url>\n" +
                "        </snapshotRepository>\n" +
                "    </distributionManagement>\n" +
                "\n" +
                "    <profiles>\n" +
                "        <profile>\n" +
                "            <id>release</id>\n" +
                "            <build>\n" +
                "                <plugins>\n" +
                "                    <plugin>\n" +
                "                        <groupId>org.apache.maven.plugins</groupId>\n" +
                "                        <artifactId>maven-gpg-plugin</artifactId>\n" +
                "                        <version>1.5</version>\n" +
                "                        <executions>\n" +
                "                            <execution>\n" +
                "                                <id>sign-artifacts</id>\n" +
                "                                <phase>verify</phase>\n" +
                "                                <goals>\n" +
                "                                    <goal>sign</goal>\n" +
                "                                </goals>\n" +
                "                            </execution>\n" +
                "                        </executions>\n" +
                "                    </plugin>\n" +
                "                    <plugin>\n" +
                "                        <groupId>org.apache.maven.plugins</groupId>\n" +
                "                        <artifactId>maven-source-plugin</artifactId>\n" +
                "                        <version>2.2.1</version>\n" +
                "                        <executions>\n" +
                "                            <execution>\n" +
                "                                <id>attach-sources</id>\n" +
                "                                <goals>\n" +
                "                                    <goal>jar-no-fork</goal>\n" +
                "                                </goals>\n" +
                "                            </execution>\n" +
                "                        </executions>\n" +
                "                    </plugin>\n" +
                "                    <plugin>\n" +
                "                        <groupId>org.apache.maven.plugins</groupId>\n" +
                "                        <artifactId>maven-javadoc-plugin</artifactId>\n" +
                "                        <version>2.9.1</version>\n" +
                "                        <executions>\n" +
                "                            <execution>\n" +
                "                                <id>attach-javadocs</id>\n" +
                "                                <goals>\n" +
                "                                    <goal>jar</goal>\n" +
                "                                </goals>\n" +
                "                            </execution>\n" +
                "                        </executions>\n" +
                "                    </plugin>\n" +
                "                </plugins>\n" +
                "            </build>\n" +
                "        </profile>\n" +
                "    </profiles>\n" +
                "\n" +
                "\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>com.tagmycode</groupId>\n" +
                "            <artifactId>tagmycode-sdk</artifactId>\n" +
                "            <version>${tagmycode-sdk.version}</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>com.intellij</groupId>\n" +
                "            <artifactId>forms_rt</artifactId>\n" +
                "            <version>5.0</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>jgoodies</groupId>\n" +
                "            <artifactId>forms</artifactId>\n" +
                "            <version>1.0.5</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>com.fifesoft</groupId>\n" +
                "            <artifactId>rsyntaxtextarea</artifactId>\n" +
                "            <version>2.5.8</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>com.tagmycode</groupId>\n" +
                "            <artifactId>tagmycode-sdk</artifactId>\n" +
                "            <version>${tagmycode-sdk.version}</version>\n" +
                "            <type>test-jar</type>\n" +
                "            <scope>test</scope>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.mockito</groupId>\n" +
                "            <artifactId>mockito-all</artifactId>\n" +
                "            <version>1.9.5</version>\n" +
                "            <scope>test</scope>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>\n");

        RTextScrollPane sp = new RTextScrollPane(textArea);
        cp.add(sp);
        sp.setLineNumbersEnabled(false);
        sp.setFoldIndicatorEnabled(false);

        setContentPane(cp);
        setTitle("Text Editor Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        textArea.setEnabled(false);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);


    }

    public static void main(String[] args) {
        // Start all Swing applications on the EDT.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TextEditorDemo().setVisible(true);
            }
        });
    }

}