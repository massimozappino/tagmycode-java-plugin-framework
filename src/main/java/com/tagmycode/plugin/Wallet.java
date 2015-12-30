package com.tagmycode.plugin;

import com.tagmycode.plugin.exception.TagMyCodeGuiException;
import com.tagmycode.sdk.IOauthWallet;
import com.tagmycode.sdk.authentication.OauthToken;

public class Wallet implements IOauthWallet {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    private IPasswordKeyChain passwordKeyChain;

    public Wallet(IPasswordKeyChain passwordKeyChain) {

        this.passwordKeyChain = passwordKeyChain;
    }

    public IPasswordKeyChain getPasswordKeyChain() {
        return passwordKeyChain;
    }

    public OauthToken loadOauthToken() throws TagMyCodeGuiException {
        String accessTokenString = passwordKeyChain.loadValue(ACCESS_TOKEN);
        String refreshTokenString = passwordKeyChain.loadValue(REFRESH_TOKEN);

        OauthToken oauthToken = null;
        if (accessTokenString != null && refreshTokenString != null) {
            oauthToken = new OauthToken(accessTokenString, refreshTokenString);
        }

        return oauthToken;
    }

    @Override
    public void saveOauthToken(OauthToken accessToken) throws TagMyCodeGuiException {
        passwordKeyChain.saveValue(ACCESS_TOKEN, accessToken.getAccessToken().getToken());
        passwordKeyChain.saveValue(REFRESH_TOKEN, accessToken.getRefreshToken().getToken());
    }

    public void deleteAccessToken() throws TagMyCodeGuiException {
        passwordKeyChain.deleteValue(ACCESS_TOKEN);
        passwordKeyChain.deleteValue(REFRESH_TOKEN);
    }
}