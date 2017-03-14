package com.tagmycode.plugin;

import support.FakePasswordKeyChain;
import com.tagmycode.sdk.authentication.OauthToken;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WalletTest {

    private final Wallet wallet;
    private OauthToken oauthToken;

    public WalletTest() {
        wallet = new Wallet(new FakePasswordKeyChain());
    }

    @Before
    public void initOauthToken() throws Exception {
        oauthToken = new OauthToken("12345", "qwerty");
    }

    @Test
    public void saveAndLoadOauthToken() throws Exception {
        wallet.saveOauthToken(oauthToken);
        assertEquals(oauthToken, wallet.loadOauthToken());
    }

    @Test
    public void saveAndDeleteAccessToken() throws Exception {
        wallet.saveOauthToken(oauthToken);
        wallet.deleteOauthToken();
        assertEquals(null, wallet.loadOauthToken());
    }

}
