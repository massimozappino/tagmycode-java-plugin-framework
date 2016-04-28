package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.IOnErrorCallback;
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

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

public class Framework implements IOnErrorCallback {
    public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final MainWindow mainWindow;
    private final Wallet wallet;
    private final Client client;
    private final TagMyCode tagMyCode;
    private final Frame parentFrame;
    private final IMessageManager messageManager;
    private final AbstractTaskFactory taskFactory;
    private final SnippetsUpdatePollingProcess polling;
    private Data data;
    private QuickSearchDialog quickSearchDialog;
    private SettingsForm settingsForm;

    public Framework(TagMyCodeApi tagMyCodeApi, FrameworkConfig frameworkConfig, AbstractSecret secret) {
        wallet = new Wallet(frameworkConfig.getPasswordKeyChain());
        client = new Client(tagMyCodeApi, secret.getConsumerId(), secret.getConsumerSecret(), wallet);
        tagMyCode = new TagMyCode(client);
        this.messageManager = frameworkConfig.getMessageManager();
        this.parentFrame = frameworkConfig.getParentFrame();
        this.taskFactory = frameworkConfig.getTask();
        this.data = new Data(new StorageEngine(frameworkConfig.getStorage()));
        this.mainWindow = new MainWindow(this);
        quickSearchDialog = new QuickSearchDialog(this, getParentFrame());
        polling = new SnippetsUpdatePollingProcess(this);
        settingsForm = new SettingsForm(this, getParentFrame());
    }

    public void start() {
        try {
            restoreData();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
        boolean initialized = isInitialized();
        LOGGER.info("is logged: " + initialized);

        mainWindow.setLoggedIn(initialized);
        if (initialized) {
            snippetsDataChanged();
            tagMyCode.setLastSnippetsUpdate(data.getLastSnippetsUpdate());
            polling.start();
        }
    }

    public void syncSnippets() {
        polling.forceScheduleUpdate();
    }

    public LoginDialog showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this, getParentFrame());
        loginDialog.display();
        return loginDialog;
    }

    public void showNewSnippetDialog(Snippet snippet, String mimeType) {
        SnippetDialog snippetDialog = new SnippetDialog(this, mimeType, getParentFrame());
        snippetDialog.populateFieldsWithSnippet(snippet);
        snippetDialog.display();
    }

    public void showEditSnippetDialog(Snippet snippet, String mimeType) {
        SnippetDialog snippetDialog = new SnippetDialog(this, mimeType, getParentFrame());
        snippetDialog.setEditableSnippet(snippet);
        snippetDialog.display();
    }

    public void showSettingsDialog() {
        settingsForm.display();
    }

    public QuickSearchDialog showSearchDialog(IDocumentInsertText documentInsertText) {
        quickSearchDialog.display();
        quickSearchDialog.setDocumentInsertText(documentInsertText);
        return quickSearchDialog;
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
        fetchBasicData();
        saveData();
    }

    protected void fetchBasicData() throws TagMyCodeException {
        data.setAccount(tagMyCode.fetchAccount());
        data.setLanguages(tagMyCode.fetchLanguages());
    }

    protected void loadData() throws TagMyCodeStorageException {
        data.loadAll();
        tagMyCode.setLastSnippetsUpdate(data.getLastSnippetsUpdate());
    }

    public void logout() {
        getMainWindow().setLoggedIn(false);
        polling.terminate();
        try {
            wallet.deleteAccessToken();
        } catch (TagMyCodeGuiException e) {
            manageTagMyCodeExceptions(e);
        } finally {
            try {
                reset();
            } catch (TagMyCodeException e) {
                manageTagMyCodeExceptions(e);
            } catch (IOException e) {
                manageTagMyCodeExceptions(new TagMyCodeException());
            }
        }
    }

    public void reset() throws TagMyCodeException, IOException {
        client.revokeAccess();
        data.clearDataAndStorage();
        tagMyCode.setLastSnippetsUpdate(null);
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
        LOGGER.severe(e.getMessage());
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
        showLoginDialog();
    }

    public boolean canOperate() {
        if (!isInitialized()) {
            showLoginDialog();
        }
        return isInitialized();
    }

    public void restoreData() throws IOException {
        try {
            loadAccessTokenFormWallet();
            loadData();
        } catch (TagMyCodeStorageException e) {
            data.clearDataAndStorage();
            LOGGER.severe(e.getMessage());
        } catch (TagMyCodeException e) {
            manageTagMyCodeExceptions(e);
        }
    }

    public void initialize(final String verificationCode) {
        mainWindow.setLoggedIn(true);

        try {
            getWallet().saveOauthToken(client.getOauthToken());
            try {
                try {
                    getClient().fetchOauthToken(verificationCode);
                } catch (TagMyCodeConnectionException e) {
                    throw new TagMyCodeGuiException("Unable to authenticate");
                }
                fetchAndStoreAllData();
                snippetsDataChanged();
                polling.start();
            } catch (TagMyCodeException ex) {
                manageTagMyCodeExceptions(ex);
                logout();
            }
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

    protected void setData(Data data) {
        this.data = data;
    }

    public StorageEngine getStorageEngine() {
        return data.getStorageEngine();
    }

    public void addSnippet(Snippet snippet) {
        getData().getSnippets().add(snippet);
        snippetsDataChanged();
        saveData();
    }

    public void updateSnippet(Snippet snippet) {
        SnippetCollection snippets = getData().getSnippets();
        snippets.updateSnippet(snippet);
        snippetsDataChanged();
    }

    public void deleteSnippet(Snippet snippetToDelete) {
        SnippetCollection snippets = getData().getSnippets();
        snippets.deleteById(snippetToDelete.getId());
        snippetsDataChanged();
    }

    public void snippetsDataChanged() {
        getData().setLastSnippetsUpdate(tagMyCode.getLastSnippetsUpdate());
        getMainWindow().getSnippetsTab().fireSnippetsChanged();
        saveData();
    }

    protected void saveData() {
        try {
            getData().saveAll();
        } catch (TagMyCodeStorageException e) {
            manageTagMyCodeExceptions(e);
        }
    }

    public void openSnippet(Snippet snippet) {
        showEditSnippetDialog(snippet, null);
    }

    public JComponent getMainFrame() {
        return mainWindow.getMainComponent();
    }

    @Override
    public void onError(TagMyCodeException e) {
        manageTagMyCodeExceptions(e);
    }
}
