package com.tagmycode.plugin;


import support.FakePasswordKeyChain;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PasswordManagerTest {

    private FakePasswordKeyChain passwordManager;

    public PasswordManagerTest() {
        passwordManager = new FakePasswordKeyChain();
    }

    @Test
    public void storeAndLoadValue() {
        String valueString = "value";
        passwordManager.saveValue("key", valueString);
        assertEquals(valueString, passwordManager.loadValue("key"));
    }

    @Test
    public void storeAndDeleteValue() {
        String valueString = "value";
        passwordManager.saveValue("key", valueString);
        passwordManager.deleteValue("key");

        assertEquals(null, passwordManager.loadValue("key"));
    }
}
