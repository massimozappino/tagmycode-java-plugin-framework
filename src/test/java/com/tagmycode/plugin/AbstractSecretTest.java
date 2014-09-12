package com.tagmycode.plugin;

import org.junit.Test;
import support.FakeSecret;

import static org.junit.Assert.assertEquals;

public class AbstractSecretTest {
    @Test
    public void getValidValues() {
        FakeSecret fakeSecret = new FakeSecret();
        assertEquals("ID", fakeSecret.getConsumerId());
        assertEquals("SECRET", fakeSecret.getConsumerSecret());
    }

}
