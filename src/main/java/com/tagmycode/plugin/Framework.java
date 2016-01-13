package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.SnippetsJTable;
import com.tagmycode.plugin.gui.form.*;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.TagMyCodeApi;
import com.tagmycode.sdk.exception.TagMyCodeConnectionException;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeUnauthorizedException;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;

import java.awt.*;
import java.io.IOException;

public class Framework {
    private final MainWindow mainWindow;
    private final Wallet wallet;
    private final Client client;
    private final TagMyCode tagMyCode;
    private final Frame parentFrame;
    private final IMessageManager messageManager;
    private final AbstractTaskFactory taskFactory;
    private final Data data;
    private SearchSnippetDialog searchSnippetDialog;
    private StorageEngine storageEngine;

    public Framework(TagMyCodeApi tagMyCodeApi, FrameworkConfig frameworkConfig, AbstractSecret secret) {
        wallet = new Wallet(frameworkConfig.getPasswordKeyChain());
        client = new Client(tagMyCodeApi, secret.getConsumerId(), secret.getConsumerSecret(), wallet);
        tagMyCode = new TagMyCode(client);
        this.messageManager = frameworkConfig.getMessageManager();
        this.parentFrame = frameworkConfig.getParentFrame();
        this.taskFactory = frameworkConfig.getTask();
        this.mainWindow = new MainWindow(this);
        this.storageEngine = new StorageEngine(frameworkConfig.getStorage());
        this.data = new Data(storageEngine);
        restoreData();
        new PollingProcess().start();
    }

    public AuthorizationDialog showAuthorizationDialog(ICallback... iCallback) {
        AuthorizationDialog authorizationDialog = new AuthorizationDialog(this, iCallback, getParentFrame());
        authorizationDialog.display();
        return authorizationDialog;
    }

    public void showSnippetDialog(Snippet snippet, String mimeType) {
        SnippetDialog snippetDialog = new SnippetDialog(this, mimeType, getParentFrame());
        snippetDialog.populateWithSnippet(snippet);
        snippetDialog.display();
    }

    public void showSearchDialog(IDocumentInsertText documentUpdate) {
        if (searchSnippetDialog == null) {
            searchSnippetDialog = new SearchSnippetDialog(documentUpdate, this, getParentFrame());
        }

        searchSnippetDialog.display();
    }

    public void showSettingsDialog() {
        new SettingsForm(this, getParentFrame()).display();
    }

    public MainWindow getMainWindow() {
        return mainWindow;
    }

    public LanguageCollection getLanguageCollection() {
        return data.getLanguages();
    }

    public void setLanguageCollection(LanguageCollection languageCollection) {
        data.setLanguages(languageCollection);
    }

    public AbstractTaskFactory getTaskFactory() {
        return taskFactory;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public Client getClient() {
        return client;
    }

    public IMessageManager getMessageManager() {
        return messageManager;
    }

    public OauthToken loadAccessTokenFormWallet() throws TagMyCodeException {
        OauthToken oauthToken = wallet.loadOauthToken();
        client.setOauthToken(oauthToken);
        return oauthToken;
    }

    public void fetchAndStoreAllData() throws TagMyCodeException {
        fetchAllData();
        storeData();
    }

    protected void fetchAllData() throws TagMyCodeException {
        data.setAccount(tagMyCode.fetchAccount());
        data.setLanguages(tagMyCode.fetchLanguages());
        data.setSnippets(tagMyCode.fetchSnippets());
    }

    protected void loadData() throws TagMyCodeStorageException {
        data.loadAll();
        updateSnippets(data.getSnippets());
    }

    protected void storeData() throws TagMyCodeStorageException {
        data.saveAll();
    }

    public void logout() {
        try {
            wallet.deleteAccessToken();
        } catch (TagMyCodeGuiException e) {
            manageTagMyCodeExceptions(e);
        } finally {
            try {
                client.revokeAccess();
                data.reset();
                storageEngine.clearAll();
            } catch (TagMyCodeException e) {
                manageTagMyCodeExceptions(e);
            } catch (IOException e) {
                manageTagMyCodeExceptions(new TagMyCodeException());
            }
        }
    }

    public boolean isInitialized() {
        return client.isAuthenticated() && data.getAccount() != null && data.getLanguages() != null;
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public void showError(final String message) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                messageManager.error(message);
            }
        });
    }

    public void showGenericError() {
        showError(new TagMyCodeException().getMessage());
    }

    public void manageTagMyCodeExceptions(TagMyCodeException e) {
        // TODO convert with Logger
        e.printStackTrace();
        if (e instanceof TagMyCodeUnauthorizedException) {
            logoutAndAuthenticateAgain();
        } else if (e instanceof TagMyCodeConnectionException || e instanceof TagMyCodeStorageException) {
            showError(e.getMessage());
        } else {
            showGenericError();
        }
    }

    public void logoutAndAuthenticateAgain() {
        logout();
        showAuthorizationDialog();
    }

    public boolean canOperate() {
        if (!isInitialized()) {
            showAuthorizationDialog();
        }
        return isInitialized();
    }

    public void restoreData() {
        try {
            loadAccessTokenFormWallet();
            loadData();
        } catch (TagMyCodeStorageException e) {
            data.reset();
            // TODO what to do? reload from server?
        } catch (TagMyCodeException e) {
            manageTagMyCodeExceptions(e);
        }
    }

    public void initialize(final String verificationCode, final ICallback[] callbacks) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        getClient().fetchOauthToken(verificationCode);
                    } catch (TagMyCodeConnectionException e) {
                        throw new TagMyCodeGuiException("Unable to authenticate");
                    }
                    fetchAndStoreAllData();
                    updateSnippets(data.getSnippets());
                } catch (TagMyCodeException ex) {
                    manageTagMyCodeExceptions(ex);
                    logout();
                } finally {
                    if (callbacks != null) {
                        for (ICallback callback : callbacks) {
                            callback.doOperation();
                        }
                    }
                }
            }
        };

        try {
            getWallet().saveOauthToken(client.getOauthToken());
            getTaskFactory().create(runnable, "Initializing TagMyCode");
        } catch (TagMyCodeGuiException e) {
            manageTagMyCodeExceptions(e);
            logoutAndAuthenticateAgain();
        }
    }

    public TagMyCode getTagMyCode() {
        return tagMyCode;
    }

    public Data getData() {
        return data;
    }

    public StorageEngine getStorageEngine() {
        return storageEngine;
    }

    public void addSnippet(Snippet snippet) {
        getData().getSnippets().add(snippet);
        getSnippetsJTable().addSnippet(snippet);
        saveData();
    }

    public void updateSnippets(SnippetCollection snippets) {
        getData().setSnippets(snippets);
        getSnippetsJTable().updateWithSnippets(snippets);
        saveData();
    }

    private SnippetsJTable getSnippetsJTable() {
        return getMainWindow().getSnippetsTab().getSnippetsJTable();
    }

    private void saveData() {
        try {
            getData().saveAll();
        } catch (TagMyCodeStorageException e) {
            manageTagMyCodeExceptions(e);
        }
    }
}
