package com.tagmycode.plugin;

import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.crash.Crash;
import com.tagmycode.sdk.crash.CrashClient;
import com.tagmycode.sdk.exception.TagMyCodeException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrashService {
    private CrashClient crashClient;
    private Data data;
    private TagMyCode tagMyCode;
    private AbstractVersion version;
    private String appId;

    public CrashService(CrashClient crashClient, Data data, TagMyCode tagMyCode, AbstractVersion version, String consumerId) {
        this.crashClient = crashClient;
        this.data = data;
        this.tagMyCode = tagMyCode;
        this.version = version;
        appId = consumerId;
    }

    public void send(final Exception exception) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(createRunnable(exception));
        executor.shutdown();
    }

    private Runnable createRunnable(final Exception exception) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String appVersion = version.getFrameworkVersion() + " " + version.getPluginVersion() + " " + version.getPluginTitle();
                    Crash crash = Crash.create(appId, appVersion, data.getAccount(), tagMyCode.getClient().getOauthToken(), exception);
                    crashClient.sendCrash(crash);
                } catch (TagMyCodeException tmcException) {
                    Framework.LOGGER.debug("Send crash exception", tmcException);
                }
            }
        };
    }
}
