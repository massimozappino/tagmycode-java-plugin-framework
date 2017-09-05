package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.plugin.gui.form.*;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.TagMyCodeApi;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeUnauthorizedException;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.Snippet;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class Framework implements IOnErrorCallback {
    public final static Logger LOGGER = Logger.getLogger(Framework.class);

    private final TagMyCode tagMyCode;
    private final Frame parentFrame;
    private final IMessageManager messageManager;
    private final AbstractTaskFactory taskFactory;
    private final SnippetsUpdatePollingProcess pollingProcess;
    private final AbstractVersion version;
    private final Data data;
    private IBrowser browser;
    private MainWindow mainWindow;
    private QuickSearchDialog quickSearchDialog;
    private SettingsForm settingsForm;
    private SnippetDialogFactory snippetDialogFactory;
    private AboutDialog aboutDialog;
    private FrameworkConfig frameworkConfig;

    public Framework(TagMyCodeApi tagMyCodeApi, FrameworkConfig frameworkConfig, AbstractSecret secret) throws SQLException {
        this.frameworkConfig = frameworkConfig;
        this.browser = frameworkConfig.getBrowser();
        Client client = new Client(tagMyCodeApi, secret.getConsumerId(), secret.getConsumerSecret(), new Wallet(frameworkConfig.getPasswordKeyChain()));
        tagMyCode = new TagMyCode(client);
        this.messageManager = frameworkConfig.getMessageManager();
        this.parentFrame = frameworkConfig.getParentFrame();
        this.taskFactory = frameworkConfig.getTask();
        StorageEngine storageEngine = new StorageEngine(frameworkConfig.getDbService());

        this.data = new Data(storageEngine);
        quickSearchDialog = new QuickSearchDialog(this, getParentFrame());
        pollingProcess = new SnippetsUpdatePollingProcess(this);
        settingsForm = new SettingsForm(this, getParentFrame());
        snippetDialogFactory = new SnippetDialogFactory();
        version = frameworkConfig.getVersionObject();
        aboutDialog = new AboutDialog(this, getParentFrame());
        this.mainWindow = new MainWindow(this);
    }

    public void start() throws IOException, TagMyCodeException, SQLException {
        restoreData();

        boolean initialized = isInitialized();
        mainWindow.setLoggedIn(initialized);
        if (initialized) {
            mainWindow.getSnippetsTab().setNetworkingEnabled(isNetworkingEnabled());
        }
    }

    public void syncSnippets() {
        pollingProcess.forceScheduleUpdate();
    }

    public LoginDialog showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this, getParentFrame());
        loginDialog.display();
        return loginDialog;
    }

    public void showNewSnippetDialog(Snippet snippet) {
        SnippetDialog snippetDialog = new SnippetDialog(this, getParentFrame());
        snippetDialog.populateFieldsWithSnippet(snippet);
        snippetDialog.display();
    }

    public SnippetDialog showEditSnippetDialog(Snippet snippet) {
        if (snippet == null) {
            return null;
        }

        SnippetDialog snippetDialog = snippetDialogFactory.create(this, getParentFrame());
        snippetDialog.setEditableSnippet(snippet);
        snippetDialog.display();
        return snippetDialog;
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

    public void setLanguageCollection(LanguagesCollection languageCollection) throws TagMyCodeStorageException {
        getStorageEngine().saveLanguageCollection(languageCollection);
    }

    public AbstractTaskFactory getTaskFactory() {
        return taskFactory;
    }

    public IMessageManager getMessageManager() {
        return messageManager;
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
        pollingProcess.terminate();
        try {
            reset();
        } catch (TagMyCodeException e) {
            manageTagMyCodeExceptions(e);
        }
    }

    void reset() throws TagMyCodeException {
        data.clearDataAndStorage();
        tagMyCode.setLastSnippetsUpdate(null);
        tagMyCode.revokeAccessToken();
        snippetsDataChanged();
    }

    public boolean isInitialized() {
        return tagMyCode.isAuthenticated() && data.getAccount() != null && data.getLanguages() != null;
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

    public void manageTagMyCodeExceptions(TagMyCodeException e) {
        logError(e);
        if (e instanceof TagMyCodeUnauthorizedException) {
            logoutAndAuthenticateAgain();
        } else {
            showError(e.getMessage());
        }
    }

    public void logError(Exception e) {
        LOGGER.error("TagMyCode Error", e);
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

    public void restoreData() throws TagMyCodeStorageException {
        try {
            tagMyCode.loadOauthToken();
            loadData();
            snippetsDataChanged();
        } catch (TagMyCodeStorageException e) {
            data.clearDataAndStorage();
            logError(e);
        } catch (TagMyCodeException e) {
            manageTagMyCodeExceptions(e);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initialize(final String verificationCode) {
        mainWindow.setLoggedIn(true);
        try {
            tagMyCode.authenticate(verificationCode);

            fetchAndStoreAllData();
            saveAndTellThatSnippetsDataAreChanged();
            if (isNetworkingEnabled()) {
                pollingProcess.start();
            }
        } catch (TagMyCodeException ex) {
            manageTagMyCodeExceptions(ex);
            logout();
        }
    }

    public TagMyCode getTagMyCode() {
        return tagMyCode;
    }

    public Data getData() {
        return data;
    }

    public StorageEngine getStorageEngine() {
        return data.getStorageEngine();
    }

    public void saveAndTellThatSnippetsDataAreChanged() {
        saveData();
        snippetsDataChanged();
    }

    public void snippetsDataChanged() {
        getData().setLastSnippetsUpdate(tagMyCode.getLastSnippetsUpdate());
        getMainWindow().getSnippetsTab().fireSnippetsChanged();
    }

    protected synchronized void saveData() {
        try {
            getData().saveAll();
        } catch (TagMyCodeStorageException e) {
            manageTagMyCodeExceptions(e);
        }
    }

    public JComponent getMainFrame() {
        return mainWindow.getMainComponent();
    }

    public void setSnippetDialogFactory(SnippetDialogFactory snippetDialogFactory) {
        this.snippetDialogFactory = snippetDialogFactory;
    }

    @Override
    public void onError(TagMyCodeException e) {
        manageTagMyCodeExceptions(e);
    }

    public void showAboutDialog() {
        aboutDialog.display();
    }

    public AbstractVersion getVersion() {
        return version;
    }

    public boolean isNetworkingEnabled() {
        return data.isNetworkingEnabled();
    }

    public synchronized void setNetworkingEnabled(boolean flag) {
        data.setNetworkingEnabled(flag);
        if (flag) {
            pollingProcess.start();
        } else {
            pollingProcess.terminate();
        }
    }

    public void closeFramework() throws TagMyCodeStorageException, IOException {
        // TODO wait for all pending tasks
        if (isInitialized()) {
            data.saveAll();
        }

        getStorageEngine().close();
        LOGGER.info("Exiting TagMyCode");
    }

    public IBrowser getBrowser() {
        return browser;
    }

    public FrameworkConfig getFrameworkConfig() {
        return frameworkConfig;
    }

    public SnippetsUpdatePollingProcess getPollingProcess() {
        return pollingProcess;
    }
}
