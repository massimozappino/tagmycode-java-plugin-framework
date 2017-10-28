package com.tagmycode.plugin;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Browser implements IBrowser {

    private Application application;

    public Browser() {
        this.application = new Application() {
            @Override
            public void start(Stage primaryStage) throws Exception {
            }
        };
    }

    @Override
    public boolean openUrl(String url) {
        HostServicesFactory.getInstance(application).showDocument(url);
        return true;
    }

}
