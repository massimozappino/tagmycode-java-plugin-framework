package com.tagmycode.plugin.examples.support;

import com.tagmycode.plugin.IPasswordKeyChain;
import com.tagmycode.plugin.exception.TagMyCodeGuiException;

import java.io.*;
import java.util.HashMap;


public class PasswordKeyChain implements IPasswordKeyChain {
    HashMap<String, String> storage = new HashMap<String, String>();
    String filename = "/tmp/tagmycode_storage.ser";

    public PasswordKeyChain()
    {
        loadFromFile();
    }

    @Override
    public void saveValue(String key, String value) throws TagMyCodeGuiException {
        storage.put(key, value);
        saveToFile();
        System.out.println(String.format("key: %s, value: %s", key, value));
    }

    @Override
    public String loadValue(String key) throws TagMyCodeGuiException {
        return storage.get(key);
    }

    @Override
    public void deleteValue(String key) throws TagMyCodeGuiException {
        storage.remove(key);
    }

    private void saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(storage);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void loadFromFile() {
        try {
            FileInputStream fis = new FileInputStream(filename);

            ObjectInputStream ois = new ObjectInputStream(fis);
            storage = (HashMap<String, String>) ois.readObject();
            ois.close();
            fis.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
