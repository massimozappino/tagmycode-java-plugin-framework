package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.form.*;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.authentication.TagMyCodeApi;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeUnauthorizedException;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.Snippet;

import java.awt.*;
import java.io.IOException;
import java.util.Calendar;

public class Framework {
    public static final int DAYS_BEFORE_RELOAD = 5;
    private final MainWindow mainWindow;
    private final Wallet wallet;
    private final Client client;
    private final TagMyCode tagMyCode;
    private final Frame parentFrame;
    private final IMessageManager messageManager;
    private final IConsole console;
    private final AbstractTaskFactory taskFactory;
    private final Data data;
    private StorageEngine storageEngine;
    private SearchSnippetDialog searchSnippetDialog;

    public Framework(TagMyCodeApi tagMyCodeApi, FrameworkConfig frameworkConfig, AbstractSecret secret) {
        wallet = new Wallet(frameworkConfig.getPasswordKeyChain());
        client = new Client(tagMyCodeApi, secret.getConsumerId(), secret.getConsumerSecret(), wallet);
        tagMyCode = new TagMyCode(client);
        // TODO remove field
        this.storageEngine = new StorageEngine(frameworkConfig.getStorage());
        this.messageManager = frameworkConfig.getMessageManager();
        this.parentFrame = frameworkConfig.getParentFrame();
        this.taskFactory = frameworkConfig.getTask();
        this.mainWindow = new MainWindow(this);
        this.console = mainWindow.getConsoleTab().getConsole();
        this.data = new Data(storageEngine);
        getConsole().log("TagMyCode started");
        restoreData();
        new PollingProcess().start();
    }

    public void showAuthenticateDialog(ICallback... iCallback) {
        new AuthorizationDialog(this, iCallback, getParentFrame()).display();
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
    }

    protected void storeData() throws TagMyCodeStorageException {
        data.saveAll();
    }

    public void logout() {
        try {
            wallet.deleteAccessToken();
        } catch (TagMyCodeGuiException e) {
            manageTagMyCodeExceptions(e);
        }
        try {
            client.revokeAccess();
            storageEngine.clearAll();
        } catch (TagMyCodeException e) {
            manageTagMyCodeExceptions(e);
        } catch (IOException e) {
            manageTagMyCodeExceptions(new TagMyCodeException());
        }
    }

    public boolean isDataToBeRefreshed() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1 * DAYS_BEFORE_RELOAD);
        return storageEngine.loadLastUpdate().before(cal.getTime());
    }

    public boolean isRefreshable() {
        return client.isAuthenticated() && isDataToBeRefreshed();
    }

    public boolean isInitialized() {
        return client.isAuthenticated() && data.getAccount() != null && data.getLanguages() != null;
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public void error(final String message) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                messageManager.error(message);
            }
        });
    }

    public void error() {
        error(new TagMyCodeException().getMessage());
    }

    public void manageTagMyCodeExceptions(TagMyCodeException exception) {
        if (exception instanceof TagMyCodeUnauthorizedException) {
            logoutAndAuthenticateAgain();
        } else {
            error();
        }
    }

    public void logoutAndAuthenticateAgain() {
        logout();
        showAuthenticateDialog();
    }

    public boolean canOperate() {
        if (!isInitialized()) {
            showAuthenticateDialog();
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

    public void initialize(final ICallback[] callbacks) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    fetchAndStoreAllData();
                    console.log(String.format("User authenticated as <strong>%s</strong>", data.getAccount().getEmail()));
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

    public IConsole getConsole() {
        return console;
    }

    public StorageEngine getStorageEngine() {
        return storageEngine;
    }

    protected void setStorageEngine(StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
    }

    public Data getData() {
        return data;
    }
}
