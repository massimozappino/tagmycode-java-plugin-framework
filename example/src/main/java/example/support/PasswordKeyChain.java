package example.support;

import com.tagmycode.plugin.IPasswordKeyChain;
import com.tagmycode.sdk.SaveFilePath;

import java.io.File;


public class PasswordKeyChain extends HashMapToFile implements IPasswordKeyChain {
    public PasswordKeyChain(SaveFilePath saveFilePath) {
        super(saveFilePath.getPath() + File.separator + "keychain");
    }
}
