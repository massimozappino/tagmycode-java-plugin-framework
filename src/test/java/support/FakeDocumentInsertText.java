package support;

import com.tagmycode.plugin.gui.IDocumentInsertText;

public class FakeDocumentInsertText implements IDocumentInsertText {
    @Override
    public void insertText(String text) {
        System.out.println(text);
    }
}
