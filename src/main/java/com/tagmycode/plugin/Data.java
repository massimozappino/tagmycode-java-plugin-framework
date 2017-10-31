package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Data {
    private StorageEngine storageEngine;
    private User account;
    private LanguagesCollection languages;
    private SnippetsCollection snippets;
    private String lastSnippetsUpdate;
    private boolean networkingEnabled;

    public Data(StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
        reset();
    }

    public void clearDataAndStorage() throws TagMyCodeStorageException {
        reset();
        storageEngine.recreateTables();
    }

    protected void reset() {
        account = null;
        languages = new DefaultLanguageCollection();
        snippets = new SnippetsCollection();
        lastSnippetsUpdate = null;
        networkingEnabled = true;
    }

    public User getAccount() {
        return account;
    }

    public void setAccount(User account) {
        this.account = account;
    }

    public LanguagesCollection getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesCollection languages) {
        this.languages = languages;
    }

    public SnippetsCollection getSnippets() {
        return snippets;
    }

    public void setSnippets(SnippetsCollection snippets) {
        this.snippets = snippets;
    }

    public void loadAll() throws TagMyCodeStorageException {
        setAccount(storageEngine.loadAccount());
        setLanguages(storageEngine.loadLanguageCollection());
        String lastSnippetsUpdate = storageEngine.loadLastSnippetsUpdate();
        setLastSnippetsUpdate(lastSnippetsUpdate);
        setSnippets(storageEngine.loadSnippets());
        setNetworkingEnabled(storageEngine.loadNetworkingEnabledFlag());
    }

    public synchronized void saveAll() throws TagMyCodeStorageException {
        storageEngine.saveAccount(getAccount());
        storageEngine.saveLanguageCollection(getLanguages());
        storageEngine.saveSnippets(getSnippets());
        storageEngine.saveLastSnippetsUpdate(getLastSnippetsUpdate());
        storageEngine.saveNetworkingEnabledFlag(isNetworkingEnabled());
    }

    public String getLastSnippetsUpdate() {
        return lastSnippetsUpdate;
    }

    public void setLastSnippetsUpdate(String lastSnippetsUpdate) {
        this.lastSnippetsUpdate = lastSnippetsUpdate;
    }

    public StorageEngine getStorageEngine() {
        return storageEngine;
    }

    public boolean isNetworkingEnabled() {
        return networkingEnabled;
    }

    public void setNetworkingEnabled(boolean networkingEnabled) {
        this.networkingEnabled = networkingEnabled;
    }

    public Snippet createSnippet(String title, String code, Language language) {
        Snippet snippet = createEmptySnippetWithLastLanguage();
        if (language != null) {
            snippet.setLanguage(language);
        }
        snippet.setCode(code);
        snippet.setTitle(title);
        return snippet;
    }

    public Snippet createEmptySnippetWithLastLanguage() {
        Snippet snippet = new Snippet();
        snippet.setLanguage(storageEngine.loadLastLanguageUsed());
        return snippet;
    }

    public Snippet createSnippetFromFile(File file) throws IOException, TagMyCodeGuiException {
        String fileName = file.getName();
        int allowedKB = 512;
        if (file.length() > allowedKB * 1024) {
            throw new TagMyCodeGuiException(String.format("File \"%s\" is larger than %dKB", fileName, allowedKB));
        }
        if (new TextEncoding().isBinaryFile(file)) {
            throw new TagMyCodeGuiException(String.format("File \"%s\" seems to be a binary file", fileName));
        }

        return createSnippet(fileName, readFile(file), getLanguages().findByFileName(fileName));
    }

    private String readFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
        return new String(encoded, Charset.defaultCharset());
    }

    public void saveSnippetToFile(File file, Snippet snippet) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(snippet.getCode().getBytes());
        out.close();
    }
}
