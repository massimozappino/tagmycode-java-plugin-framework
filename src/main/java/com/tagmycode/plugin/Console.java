package com.tagmycode.plugin;


import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Console implements IConsole {
    private JEditorPane editorPane;
    private final EditorKit editor;

    public Console(final JEditorPane editorPane) {
        this.editorPane = editorPane;
        HTMLEditorKit kit = new HTMLEditorKit();
        editorPane.setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("pre {font : 10px monaco; }");

        Document doc = kit.createDefaultDocument();
        editorPane.setDocument(doc);

        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        new Browser().openUrl(e.getURL().toURI().toString());
                    } catch (URISyntaxException ignored) {
                    }
                }
            }
        });
        editor = editorPane.getEditorKit();
    }

    @Override
    public void log(String message) {
        write(message);
    }

    private void write(String line) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String nowString = df.format(Calendar.getInstance().getTime());

        StringReader reader = new StringReader("<pre>" + nowString + " - " + line);
        try {
            editor.read(reader, editorPane.getDocument(), editorPane.getDocument().getLength());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFullLog() {
        return editorPane.getText();
    }

    public String getValue() {
        return editorPane.getText();
    }
}
