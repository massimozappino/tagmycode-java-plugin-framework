package com.tagmycode.plugin;

import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.sdk.authentication.OauthToken;

public class Wallet {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    private IPasswordKeyChain passwordManager;

    public IPasswordKeyChain getPasswordManager() {
        return passwordManager;
    }

    public Wallet(IPasswordKeyChain passwordManager) {

        this.passwordManager = passwordManager;
    }

    public OauthToken loadOauthToken() throws TagMyCodeGuiException {
        String accessTokenString = passwordManager.loadValue(ACCESS_TOKEN);
        String refreshTokenString = passwordManager.loadValue(REFRESH_TOKEN);

        OauthToken oauthToken = null;
        if (accessTokenString != null && refreshTokenString != null) {
            oauthToken = new OauthToken(accessTokenString, refreshTokenString);
        }

        return oauthToken;
    }

    public void saveOauthToken(OauthToken accessToken) throws TagMyCodeGuiException {
        passwordManager.saveValue(ACCESS_TOKEN, accessToken.getAccessToken().getToken());
        passwordManager.saveValue(REFRESH_TOKEN, accessToken.getRefreshToken().getToken());
    }

    public void deleteAccessToken() throws TagMyCodeGuiException {
        passwordManager.deleteValue(ACCESS_TOKEN);
        passwordManager.deleteValue(REFRESH_TOKEN);
    }
}