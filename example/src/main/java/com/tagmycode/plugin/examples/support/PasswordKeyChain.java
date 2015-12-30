package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.IPasswordKeyChain;


public class PasswordKeyChain extends HashMapToFile implements IPasswordKeyChain {
    public PasswordKeyChain()
    {
        super("/tmp/tagmycode_secrets.ser");
    }
}
