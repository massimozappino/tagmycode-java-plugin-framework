package com.tagmycode.plugin;


import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.plugin.gui.IDocumentInsertText;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.plugin.gui.form.*;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.TagMyCodeApi;
import com.tagmycode.sdk.crash.CrashClient;
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
    private AboutDialog aboutDialog;
    private SnippetDialog snippetDialog;
    private final CrashService crashService;
    private final SyntaxSnippetEditorFactory syntaxSnippetEditorFactory;
    private UserPreferences userPreferences;

    public Framework(TagMyCodeApi tagMyCodeApi, FrameworkConfig frameworkConfig, AbstractSecret secret) throws SQLException {
        this.browser = frameworkConfig.getBrowser();
        Client client = new Client(tagMyCodeApi, secret.getConsumerId(), secret.getConsumerSecret(), new Wallet(frameworkConfig.getPasswordKeyChain()));
        tagMyCode = new TagMyCode(client);
        this.messageManager = frameworkConfig.getMessageManager();
        this.parentFrame = frameworkConfig.getParentFrame();
        this.taskFactory = frameworkConfig.getTask();
        userPreferences = new UserPreferences("prefs.txt");
        StorageEngine storageEngine = new StorageEngine(frameworkConfig.getDbService());
        this.data = new Data(storageEngine);
        syntaxSnippetEditorFactory = new SyntaxSnippetEditorFactory(loadThemeFile(storageEngine), storageEngine.loadEditorFontSize());
        quickSearchDialog = new QuickSearchDialog(this, getParentFrame());
        pollingProcess = new SnippetsUpdatePollingProcess(this);
        version = frameworkConfig.getVersionObject();
        aboutDialog = new AboutDialog(this, getParentFrame());
        snippetDialog = new SnippetDialog(this, getParentFrame());
        this.mainWindow = new MainWindow(this);
        crashService = new CrashService(new CrashClient(client), data, tagMyCode, version, secret.getConsumerId());
    }

    private String loadThemeFile(StorageEngine storageEngine) {
        String themeFile = "";
        try {
            themeFile = storageEngine.loadEditorTheme();
        } catch (TagMyCodeStorageException ignored) {
        }
        return themeFile;
    }

    public SyntaxSnippetEditorFactory getSyntaxSnippetEditorFactory() {
        return syntaxSnippetEditorFactory;
    }

    public void start() throws TagMyCodeException {
        restoreData();

        boolean initialized = isInitialized();
        mainWindow.setLoggedIn(initialized);
        if (initialized) {
            mainWindow.getSnippetsPanel().setNetworkingEnabled(isNetworkingEnabled());
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

    public SnippetDialog showSnippetDialog(Snippet snippet) {
        // TODO check if can operate

        snippetDialog.setSnippet(snippet);
        snippetDialog.display();
        return snippetDialog;
    }

    public void showSettingsDialog() {
        new SettingsForm(this, getParentFrame())
                .display();
    }

    public QuickSearchDialog showSearchDialog(IDocumentInsertText documentInsertText) {
        // TODO check if can operate

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
        resetLastSnippetsUpdate();
        tagMyCode.revokeAccessToken();
        snippetsDataChanged();
    }

    public void resetLastSnippetsUpdate() {
        tagMyCode.setLastSnippetsUpdate(null);
        data.setLastSnippetsUpdate(null);
    }

    public boolean isInitialized() {
        return tagMyCode.isAuthenticated() && data.getAccount() != null && data.getLanguages() != null;
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public void showErrorDialog(final String message) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                messageManager.errorDialog(message);
            }
        });
    }

    public void writeErrorLog(final String message) {
        new GuiThread().execute(new Runnable() {
            @Override
            public void run() {
                messageManager.errorLog(message);
            }
        });
    }

    public void manageTagMyCodeExceptions(TagMyCodeException e) {
        logError(e);
        if (e instanceof TagMyCodeUnauthorizedException) {
            showErrorDialog(e.getMessage());
            logoutAndAuthenticateAgain();
        } else if (e instanceof TagMyCodeGuiException) {
            showErrorDialog(e.getMessage());
        } else {
            writeErrorLog(e.getMessage());
        }
    }

    public void logError(Exception e) {
        LOGGER.error("TagMyCode Error", e);
        crashService.send(e);
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
    }

    public void initialize(final String verificationCode) throws TagMyCodeException {
        tagMyCode.authenticate(verificationCode);

        try {
            mainWindow.setLoggedIn(true);
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
        getMainWindow().getSnippetsPanel().fireSnippetsChanged();
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

    public void closeFramework() throws TagMyCodeStorageException {
        // TODO wait for all pending tasks
        if (isInitialized()) {
            data.saveAll();
        }

        getStorageEngine().close();
        LOGGER.info("Exiting TagMyCode");
    }

    public void openUrlInBrowser(String url) {
        boolean urlOpened = browser.openUrl(url);
        if (!urlOpened) {
            logError(new TagMyCodeException("Unable to open link in browser, please open it manually: " + url));
        }
    }

    public SnippetsUpdatePollingProcess getPollingProcess() {
        return pollingProcess;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }
}
