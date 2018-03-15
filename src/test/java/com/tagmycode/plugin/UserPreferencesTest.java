package com.tagmycode.plugin;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class UserPreferencesTest {

    @Test
    public void booleanValue() {
        UserPreferences userPreferences = new UserPreferences(new File(""));
        assertTrue(userPreferences.getBoolean("not_existing_key", true));
        assertFalse(userPreferences.getBoolean("not_existing_key", false));

        userPreferences.setBoolean("key", true);
        assertTrue(userPreferences.getBoolean("key", false));
    }

}