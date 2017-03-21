package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.plugin.gui.form.*;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.TagMyCodeApi;
import com.tagmycode.sdk.exception.TagMyCodeApiException;
import com.tagmycode.sdk.exception.TagMyCodeConnectionException;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.exception.TagMyCodeUnauthorizedException;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.SnippetCollection;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class Framework implements IOnErrorCallback {
    public final static Logger LOGGER = Logger.getLogger(Framework.class);

    static {
        BasicConfigurator.configure();
    }

    private final TagMyCode tagMyCode;
    private final Frame parentFrame;
    private final IMessageManager messageManager;
    private final AbstractTaskFactory taskFactory;
    private final SnippetsUpdatePollingProcess pollingProcess;
    private final IVersion version;
    private final Data data;
    private IBrowser browser;
    private MainWindow mainWindow;
    private QuickSearchDialog quickSearchDialog;
    private SettingsForm settingsForm;
    private SnippetDialogFactory snippetDialogFactory;
    private AboutDialog aboutDialog;
    private FrameworkConfig frameworkConfig;

    public Framework(TagMyCodeApi tagMyCodeApi, FrameworkConfig frameworkConfig, AbstractSecret secret, String namespace) throws SQLException {
        this.frameworkConfig = frameworkConfig;
        this.browser = frameworkConfig.getBrowser();
        Client client = new Client(tagMyCodeApi, secret.getConsumerId(), secret.getConsumerSecret(), new Wallet(frameworkConfig.getPasswordKeyChain()));
        tagMyCode = new TagMyCode(client);
        this.messageManager = frameworkConfig.getMessageManager();
        this.parentFrame = frameworkConfig.getParentFrame();
        this.taskFactory = frameworkConfig.getTask();
        StorageEngine storageEngine = new StorageEngine(frameworkConfig.getDbService());
        frameworkConfig.getDbService().initialize();
        this.data = new Data(storageEngine);
        quickSearchDialog = new QuickSearchDialog(this, getParentFrame());
        pollingProcess = new SnippetsUpdatePollingProcess(this);
        settingsForm = new SettingsForm(this, getParentFrame());
        snippetDialogFactory = new SnippetDialogFactory();
        version = new DefaultVersion();
        aboutDialog = new AboutDialog(this, getParentFrame());
        this.mainWindow = new MainWindow(this);
    }

    public void start() throws IOException, TagMyCodeException, SQLException {
        restoreData();

        boolean initialized = isInitialized();
        mainWindow.setLoggedIn(initialized);
        if (initialized) {
            mainWindow.getSnippetsTab().setNetworkingEnabled(isNetworkingEnabled());
            saveSnippetsDataChanged();
            tagMyCode.setLastSnippetsUpdate(data.getLastSnippetsUpdate());
            if (isNetworkingEnabled()) {
                pollingProcess.start();
            }
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

    public void showEditSnippetDialog(Snippet snippet) {
        if (snippet == null) {
            return;
        }

        SnippetDialog snippetDialog = snippetDialogFactory.create(this, getParentFrame());
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

    public void setLanguageCollection(LanguageCollection languageCollection) throws TagMyCodeStorageException {
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
        try {
            data.clearDataAndStorage();
        } catch (SQLException | IOException e) {
           throw new TagMyCodeException(e);
        }
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

    public void showGenericError() {
        showError(new TagMyCodeException().getMessage());
    }

    public void manageTagMyCodeExceptions(TagMyCodeException e) {
        logError(e);
        if (e instanceof TagMyCodeUnauthorizedException) {
            logoutAndAuthenticateAgain();
        } else if (e instanceof TagMyCodeConnectionException || e instanceof TagMyCodeStorageException || e instanceof TagMyCodeApiException) {
            showError(e.getMessage());
        } else {
            showGenericError();
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

    public void restoreData() throws IOException, SQLException {
        try {
            tagMyCode.loadOauthToken();
            loadData();
        } catch (TagMyCodeStorageException e) {
            data.clearDataAndStorage();
            logError(e);
        } catch (TagMyCodeException e) {
            manageTagMyCodeExceptions(e);
        }
    }

    public void initialize(final String verificationCode) {
        mainWindow.setLoggedIn(true);
        try {
            tagMyCode.authenticate(verificationCode);

            fetchAndStoreAllData();
            saveSnippetsDataChanged();
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

    public void addSnippet(Snippet snippet) {
        getData().getSnippets().add(snippet);
        saveSnippetsDataChanged();
        saveData();
    }

    public void updateSnippet(Snippet snippet) {
        SnippetCollection snippets = getData().getSnippets();
        snippets.updateSnippet(snippet);
        saveSnippetsDataChanged();
    }

    public void deleteSnippet(Snippet snippetToDelete) {
        SnippetCollection snippets = getData().getSnippets();
        snippets.deleteById(snippetToDelete.getId());
        saveSnippetsDataChanged();
    }

    public void saveSnippetsDataChanged() {
        snippetsDataChanged();
        saveData();
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

    public IVersion getVersion() {
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
}
